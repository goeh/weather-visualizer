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
package se.technipelago.weather.template;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Goran Ehrsson <goran@technipelago.se>
 */
public class PageMaker {

    private static final Logger log = Logger.getLogger(PageMaker.class.getName());
    private String templateDir;
    private String outputDir;

    public void setTemplateDirectory(final String dir) {
        this.templateDir = dir;
    }

    public void setOutputDirectory(final String dir) {
        this.outputDir = dir;
    }

    public String[] generateHTML(Map<String, Object> data) throws IOException {
        Locale locale = Locale.getDefault();
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File(templateDir));
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        cfg.setSharedVariable("locale", new SimpleScalar(locale.toString()));
        cfg.setSharedVariable("lang", new SimpleScalar(locale.getLanguage()));

        List<String> files = new ArrayList<String>();
        try {
            // HTML pages.
            //generatePage(data, "index.ftl", "index.htm");
            //files.add("index.htm");
            //generatePage(data, "history.ftl", "history.htm");
            //files.add("history.htm");
            generatePage(data, cfg, "raw.ftl", "raw.htm");
            files.add("raw.htm");
            generatePage(data, cfg, "raw.ftl", "raw.txt");
            files.add("raw.txt");
            generatePage(data, cfg, "swedweather.ftl", "swedweather.txt");
            files.add("swedweather.txt");
            //generatePage(data, "select.ftl", "select.php");
            //files.add("select.php");
            // CommunityLib / Mainloop
            generatePage(data, cfg, "history2.ftl", "history2.htm");
            files.add("history2.htm");
            generatePage(data, cfg, "select2.ftl", "select2.php");
            files.add("select2.php");
            generatePage(data, cfg, "compact.ftl", "compact.txt");
            files.add("compact.txt");
            generatePage(data, cfg, "index2.ftl", "index2.txt");
            files.add("index2.txt");
        } catch (IOException e) {
            log.log(Level.SEVERE, "I/O Error", e);
        }

        return files.toArray(new String[files.size()]);
    }

    public void generatePage(final Map<String, Object> data, final Configuration cfg, final String template, final String filename) throws IOException {
        try (Writer out = new OutputStreamWriter(new FileOutputStream(outputDir != null ? outputDir + "/" + filename : filename), StandardCharsets.UTF_8)) {
            cfg.getTemplate(template).process(data, out);
            out.flush();
        } catch (TemplateException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
}
