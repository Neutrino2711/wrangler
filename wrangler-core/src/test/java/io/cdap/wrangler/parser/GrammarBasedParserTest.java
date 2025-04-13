/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package io.cdap.wrangler.parser;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.CompileStatus;
import io.cdap.wrangler.api.Compiler;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveLoadException;
import io.cdap.wrangler.api.RecipeParser;
import io.cdap.wrangler.api.RecipeException;
import io.cdap.wrangler.registry.CompositeDirectiveRegistry;
import io.cdap.wrangler.registry.SystemDirectiveRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests {@link GrammarBasedParser}
 */
public class GrammarBasedParserTest {

  @Test
  public void testBasic() throws Exception {
    String[] recipe = new String[] {
        "#pragma version 2.0;",
        "rename :col1 :col2",
        "parse-as-csv :body ',' true;",
        "#pragma load-directives text-reverse, text-exchange;",
        " ",
        "${macro_}"
    };

    RecipeParser parser = TestingRig.parse(recipe);
    List<Directive> directives = parser.parse();
    Assert.assertEquals(2, directives.size());
  }

  @Test
  public void testLoadableDirectives() throws Exception {
    String[] recipe = new String[] {
        "#pragma version 2.0;",
        "#pragma load-directives text-reverse, text-exchange;",
        "rename col1 col2",
        "parse-as-csv body , true",
        "text-reverse :body;",
        "test prop: { a='b', b=1.0, c=true};",
        "#pragma load-directives test-change,text-exchange, test1,test2,test3,test4;"
    };

    Compiler compiler = new RecipeCompiler();
    CompileStatus status = compiler.compile(new MigrateToV2(recipe).migrate());
    Assert.assertEquals(7, status.getSymbols().getLoadableDirectives().size());
  }

  @Test
  public void testCommentOnlyRecipe() throws Exception {
    String[] recipe = new String[] {
        "// test"
    };

    RecipeParser parser = TestingRig.parse(recipe);
    List<Directive> directives = parser.parse();
    Assert.assertEquals(0, directives.size());
  }

  @Test
  public void testParseRecipeWithByteSizeAndTimeDuration() throws RecipeException, DirectiveLoadException {
    String[] directives = new String[] {
        "set-column :transfer_size 1.5MB",
        "set-column :response_time 250ms"
    };

    GrammarBasedParser parser = new GrammarBasedParser("default", directives,
        new CompositeDirectiveRegistry(new SystemDirectiveRegistry()), new NoOpDirectiveContext());
    List<Directive> directiveList = parser.parse();

    Assert.assertEquals(2, directiveList.size());
  }

  @Test
  public void testParseAggregateStatsDirective() throws RecipeException, DirectiveLoadException {
    String[] directives = new String[] {
        "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    GrammarBasedParser parser = new GrammarBasedParser("default", directives,
        new CompositeDirectiveRegistry(new SystemDirectiveRegistry()), new NoOpDirectiveContext());
    List<Directive> directiveList = parser.parse();

    Assert.assertEquals(1, directiveList.size());
    Assert.assertEquals("aggregate-stats", directiveList.get(0).toString());
  }

  @Test
  public void testParseAggregateStatsWithCustomUnits() throws RecipeException, DirectiveLoadException {
    String[] directives = new String[] {
        "aggregate-stats :data_transfer_size :response_time total_size_tb total_time_min TB m"
    };

    GrammarBasedParser parser = new GrammarBasedParser("default", directives,
        new CompositeDirectiveRegistry(new SystemDirectiveRegistry()), new NoOpDirectiveContext());
    List<Directive> directiveList = parser.parse();

    Assert.assertEquals(1, directiveList.size());
  }

  @Test(expected = RecipeException.class)
  public void testInvalidByteSizeUnit() throws RecipeException, DirectiveLoadException {
    String[] directives = new String[] {
        "set-column :transfer_size 100XB" // XB is not a valid byte unit
    };

    GrammarBasedParser parser = new GrammarBasedParser("default", directives,
        new CompositeDirectiveRegistry(new SystemDirectiveRegistry()), new NoOpDirectiveContext());
    parser.parse();
  }

  @Test(expected = RecipeException.class)
  public void testInvalidTimeDurationUnit() throws RecipeException, DirectiveLoadException {
    String[] directives = new String[] {
        "set-column :response_time 100y" // y is not a valid time unit
    };

    GrammarBasedParser parser = new GrammarBasedParser("default", directives,
        new CompositeDirectiveRegistry(new SystemDirectiveRegistry()), new NoOpDirectiveContext());
    parser.parse();
  }

}
