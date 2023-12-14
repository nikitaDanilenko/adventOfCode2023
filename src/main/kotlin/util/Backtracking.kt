package util

import java.math.BigInteger

object Backtracking {


    data class RoseTree<A>(
        val node: A,
        val children: Lazy<List<RoseTree<A>>>
    )

    fun <A> generate(
        initial: A,
        extend: (A) -> List<A>
    ): RoseTree<A> =
        RoseTree(
            initial,
            lazy { extend(initial).map { n -> generate(n, extend) } }
        )

    fun <A> pruneCountOnly(
        tree: RoseTree<A>,
        canBeDismissed: (A) -> Boolean,
        isValid: (A) -> Boolean
    ): BigInteger {
        val visited = mutableSetOf<A>()

        fun collectValid(
            tree: RoseTree<A>,
            valid: BigInteger
        ): BigInteger =
            if (canBeDismissed(tree.node)) valid
            else if (isValid(tree.node))
                if (visited.contains(tree.node))
                    valid
                else {
                    visited.add(tree.node)
                    valid + BigInteger.ONE
                }
            else valid + tree.children.value.sumOf { collectValid(it, valid) }

        return collectValid(tree, BigInteger.ZERO)
    }

}