/*
 *  Copyright 2006 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package se.technipelago.weather.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * File Transfer Utility.
 * @author Goran Ehrsson <goran@technipelago.se>
 */
public class FtpTransport {

    private static final Logger log = Logger.getLogger(FtpTransport.class.getName());
    private static final Pattern URL_PATTERN = Pattern.compile("//([\\w\\.]+):([\\w\\.]+)@([^/]+)");
    private FTPClient ftp = new FTPClient();

    public void sendFiles(String url, File[] files) throws IOException {

        String username = null;
        String password = null;
        String dir = null;

        /*
         * Check if the address contains username and password. This is how you
         * enter it in Internet Explorer. ftp://user:password@ftpserver/url-path
         */
        Matcher m = URL_PATTERN.matcher(url);
        if (m.find()) {
            dir = url.substring(m.end());
            username = m.group(1);
            password = m.group(2);
            url = m.group(3);
        }

        try {
            ftp.connect(url);
            log.fine("Connected to " + url);
            log.finer(ftp.getReplyString());

            /*
             * After connection attempt, you should check the reply code to
             * verify success.
             */
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new RuntimeException("[" + reply + "] FTP server " + url + " refused connection.");
            }

            if (username == null) {
                username = "anonymous";
                password = "anonymous";
            }

            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new RuntimeException("[" + reply + "] login failed for " + username);
            }

            if (dir != null) {
                ftp.changeWorkingDirectory(dir);
                reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    throw new RuntimeException("[" + reply + "] Failed to change working directory to " + dir);
                }
                log.fine("Successfully changed working directory to " + dir);
            }

            // Are extra commands specified for this file transfer?
            // For example: quote site recfm=fb;quote site lrecl=100
            //
            String cmds = null;
            if (cmds != null) {
                String[] cmdArr = cmds.split(";");
                for (int i = 0; i < cmdArr.length; i++) {
                    String cmd = cmdArr[i];
                    // Is it a site command?
                    if (cmd.toLowerCase().startsWith("quote site")) {
                        ftp.site(cmd.substring(11));
                    } else if (cmd.toLowerCase().startsWith("site")) {
                        ftp.site(cmd.substring(5));
                    } else {
                        // Separate the command from it's arguments.
                        String[] args = cmd.split("\\s+", 2);
                        if (args.length > 1) {
                            ftp.sendCommand(args[0], args[1]);
                        } else {
                            ftp.sendCommand(args[0]);
                        }
                    }
                    reply = ftp.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(reply)) {
                        throw new IOException("[" + reply + "] Command failed: " + cmd);
                    }
                }
            }

            // Now we are ready to transfer the file.
            doTransfer(files);

            // Transfer done, goodbye.
            ftp.logout();
            log.fine("Disconnected from " + url);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                // do nothing
                }
            }
        }

    }

    private void doTransfer(File[] files) throws IOException {
        for (File file : files) {
            sendFile(file.getAbsolutePath());
            log.fine("Sent file " + file.getName());
        }
    }

    private void sendFile(String path) throws IOException {
        ftp.setFileType(FTP.BINARY_FILE_TYPE);

        String name = getBaseName(path);

        InputStream in = new BufferedInputStream(new FileInputStream(path));
        try {
            if (!ftp.storeFile(name, in)) {
                throw new IOException("Failed to store local file '" + path + "' as remote file '" + name + "'. Reason: " + ftp.getReplyString());
            }
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Returns a filename without the leading path.
     */
    private String getBaseName(final String path) {
        int idx1 = path.lastIndexOf('/');
        int idx2 = path.lastIndexOf('\\');
        int idx = Math.max(idx1, idx2);
        return idx != -1 ? path.substring(idx + 1) : path;
    }
}
