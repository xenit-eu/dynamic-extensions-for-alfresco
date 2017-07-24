//REPLACED BY JAVA. Nothing against Kotlin, just no reason to use multiple languages with only a few classes of one
// in the same project.
//package com.github.dynamicextensionsalfresco.osgi
//
//import org.slf4j.LoggerFactory
//import java.util.*
//
///**
// * Generic dependency sorter based on imports and exports.
// *
// * @author Laurent Van der Linden.
// */
//public object DependencySorterOld {
//    val logger = LoggerFactory.getLogger(javaClass)
//
//    fun <T> sort(input: Collection<T>, metadataPovider: DependencyMetadataProvider<T>): Collection<T> {
//        val result = ArrayList<T>()
//
//        val exports = collectExports(input, metadataPovider)
//
//        val visitedItems = ArrayList<T>()
//
//        for (item in input) {
//            visit(item, exports, result, visitedItems, metadataPovider)
//        }
//
//        if (logger.isDebugEnabled) {
//            logger.debug("sorting ${input.firstOrNull()}:")
//            logger.debug("  input")
//            for (item in input) {
//                logger.debug("  - $item")
//            }
//            logger.debug("  output")
//            for (item in result) {
//                logger.debug("  - $item")
//            }
//        }
//
//        return result
//    }
//
//    private fun <T> collectExports(items: Collection<T>, metadataProvider: DependencyMetadataProvider<T>): Map<Any,T> {
//        val result = HashMap<Any, T>()
//
//        for (item in items) {
//            val exports = metadataProvider.exports(item)
//            for (export in exports) {
//                result.put(export, item)
//            }
//        }
//
//        return result
//    }
//
//    private fun <T> visit(item: T, exports: Map<Any, T>, output: MutableList<T>,
//                      visited: ArrayList<T>, metadataProvider: DependencyMetadataProvider<T>) {
//        visited.add(item)
//        val imports = metadataProvider.imports(item)
//        for (import in imports) {
//            val exportingItem = exports.get(import)
//            if (exportingItem != null && exportingItem != item && !output.contains(exportingItem)) {
//                logger.debug("visit {} from {} for import {}", exportingItem, item, import)
//                if (!visited.contains(exportingItem)) {
//                    visit(exportingItem, exports, output, visited, metadataProvider)
//                } else if (!metadataProvider.allowCircularReferences) {
//                    throw IllegalArgumentException(
//                            "Circular dependency detected between %s and %s for import %s".format(item, exportingItem, import)
//                    )
//                }
//            }
//        }
//
//        if (!output.contains(item)) {
//            output.add(item)
//        }
//    }
//}
//
//interface DependencyMetadataProvider<T> {
//    val allowCircularReferences: Boolean
//
//    fun imports(item: T): Collection<Any>
//
//    fun exports(item: T): Collection<Any>
//}