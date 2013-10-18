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

import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.PropertiesUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vladislav.Rassokhin
 */
public class PluginXmlPropertiesReferenceProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof XmlAttributeValue) && !(element instanceof XmlToken)) {
      return PsiReference.EMPTY_ARRAY;
    }
    final String text;
    if (element instanceof XmlAttributeValue) {
      final XmlAttributeValue xmlAttributeValue = (XmlAttributeValue) element;
      text = xmlAttributeValue.getValue();
    } else {
      final XmlToken token = (XmlToken) element;
      text = token.getText();
    }

    if (text == null || text.isEmpty() || text.charAt(0) != '%') {
      return PsiReference.EMPTY_ARRAY;
    }
    final String name = text.substring(1);

    final PsiFile file = element.getContainingFile();
    final PsiDirectory dir = file.getParent();
    if (dir == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    if (file.getVirtualFile() == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    final String namePrefix = file.getVirtualFile().getNameWithoutExtension();
    final PropertiesFile pf = PropertiesUtil.getPropertiesFile(dir.findFile(namePrefix + ".properties"));
    if (pf == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    final IProperty property = pf.findPropertyByKey(name);
    if (property == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    return new PsiReference[]{new PsiReferenceBase.Immediate<PsiElement>(element, property.getPsiElement())};
  }

}
