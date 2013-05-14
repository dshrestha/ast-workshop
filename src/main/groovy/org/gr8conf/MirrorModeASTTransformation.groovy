/*
 * Copyright 2003-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gr8conf

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class MirrorModeASTTransformation extends AbstractASTTransformation {
    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        if (nodes.length != 2) return
        if (nodes[0] instanceof AnnotationNode && nodes[1] instanceof MethodNode) {
            def trn = new MirrorTransformer(source)
            trn.visitMethod(nodes[1])
        }
    }

    private static class MirrorTransformer extends ClassCodeExpressionTransformer {
        private final SourceUnit source;

        MirrorTransformer(final SourceUnit source) {
            this.source = source
        }

        @Override
        protected SourceUnit getSourceUnit() {
            source
        }

        @Override
        Expression transform(final Expression exp) {
            def res = super.transform(exp)
            if (res instanceof MethodCallExpression && res.method instanceof ConstantExpression) {
                res.method = new ConstantExpression(res.method.text.reverse())
            }

            res
        }
    }
}
