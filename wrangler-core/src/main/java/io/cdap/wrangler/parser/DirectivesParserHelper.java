/*
 * Copyright © 2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.api.LazyNumber;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.Numeric;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenGroup;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Helper class for DirectivesParser to handle ByteSize and TimeDuration tokens.
 */
public class DirectivesParserHelper {

  /**
   * Parse a value context and return the appropriate token.
   * 
   * @param ctx the value context from the parser
   * @return a TokenGroup containing the appropriate token
   */
  public static TokenGroup parseValue(DirectivesParser.ValueContext ctx) {
    if (ctx.Identifier() != null) {
      String text = ctx.Identifier().getText();
      return new TokenGroup(new Text(text));
    } else if (ctx.Number() != null) {
      String text = ctx.Number().getText();
      return new TokenGroup(new Numeric(new LazyNumber(text)));
    } else if (ctx.String() != null) {
      String text = ctx.String().getText();
      // Strip the quotes
      String stripped = text.substring(1, text.length() - 1);
      return new TokenGroup(new Text(StringEscapeUtils.unescapeJava(stripped)));
    } else if (ctx.BYTE_SIZE() != null) {
      String text = ctx.BYTE_SIZE().getText();
      return new TokenGroup(new ByteSize(text));
    } else if (ctx.TIME_DURATION() != null) {
      String text = ctx.TIME_DURATION().getText();
      return new TokenGroup(new TimeDuration(text));
    } else {
      throw new IllegalArgumentException("Unknown value type: " + ctx.getText());
    }
  }
}
