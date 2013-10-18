/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.intellij.eclipse.development.extra;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.XmlFilePattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.xml.XmlToken;

import static com.intellij.patterns.StandardPatterns.string;
import static com.intellij.patterns.XmlPatterns.*;

/**
 * @author Vladislav.Rassokhin
 */
public class PluginXmlPropertiesProvider extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
    final XmlFilePattern.Capture filePattern = xmlFile().withName(string().oneOf("plugin.xml", "feature.xml", "site.xml"));

    registrar.registerReferenceProvider(
        XmlPatterns.xmlAttributeValue().inFile(filePattern).withValue(string().startsWith("%")),
        new PluginXmlPropertiesReferenceProvider()
    );
    registrar.registerReferenceProvider(
        PlatformPatterns.psiElement(XmlToken.class).inside(
            xmlText().inside(
                xmlTag().inFile(filePattern)
            )
        ).withText(string().startsWith("%")),
        new PluginXmlPropertiesReferenceProvider()
    );
  }
}
