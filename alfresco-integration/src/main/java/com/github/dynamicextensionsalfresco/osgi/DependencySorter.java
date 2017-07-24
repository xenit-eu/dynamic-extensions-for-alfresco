package com.github.dynamicextensionsalfresco.osgi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasper on 17/07/17.
 */
public class DependencySorter {
    public static Logger logger = LoggerFactory.getLogger(com.github.dynamicextensionsalfresco.osgi.DependencySorter.class);

    public static <T> Collection<T> sort(Collection<T> input, DependencyMetadataProvider<T> metadataProvider) {
        ArrayList<T> result = new ArrayList<T>();
        Map<Object, T> exports = collectExports(input, metadataProvider);
        ArrayList<T> visitedItems = new ArrayList<T>();

        for (T item : input) {
            DependencySorter.visit(item, exports, result, visitedItems, metadataProvider);
        }
        LogDebug(input, result);
        return result;
    }

    private static <T> void LogDebug(Collection<T> input, ArrayList<T> result) {
        if (logger.isDebugEnabled()) {
            logger.debug("sorting ${input.firstOrNull()}:");
            logger.debug("  input");
            for (T item : input) {
                logger.debug("  - $item");
            }
            logger.debug("  output");
            for (T item : result) {
                logger.debug("  - $item");
            }
        }
    }


    private static <T> void visit(
            T item,
            Map<Object, T> exports,
            ArrayList<T> output,
            ArrayList<T> visited,
            DependencyMetadataProvider<T> metadataProvider) {
        visited.add(item);
        Collection<Object> imports = metadataProvider.imports(item);
        for (Object importz : imports) {
            T exportingItem = exports.get(importz);
            if (exportingItem != null && exportingItem != item && !output.contains(exportingItem)) {
                logger.debug("visit {} from {} for import {}", exportingItem, item, importz);
                if (!visited.contains(exportingItem)) {
                    visit(exportingItem, exports, output, visited, metadataProvider);
                } else if (!metadataProvider.allowCircularReferences()) {
                    String message = String.format("Circular dependency detected between %s and %s for import %s", item, exportingItem, importz);
                    throw new IllegalArgumentException(message);

                }
            }
        }

        if (!output.contains(item)) {
            output.add(item);
        }
    }

    private static <T> Map<Object, T> collectExports(Collection<T> items, DependencyMetadataProvider<T> metadataProvider) {
        HashMap<Object, T> result = new HashMap<Object, T>();

        for (T item : items) {
            Collection<Object> exports = metadataProvider.exports(item);
            for (Object export : exports) {
                result.put(export, item);
            }
        }
        return result;
    }

}
