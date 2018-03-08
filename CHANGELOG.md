# Dynamic Extensions For Alfresco Changelog

## [1.7.3] - 2017-12-22
### New features
* [#167](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issue/167) Make WebScripts work with HttpEntity return value
* [#169](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issue/169) Add an issue and pull request template


## [1.7.2] - 2017-12-11
### Bug fixes
* [#156](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issue/156) NullPointerException in GenericQuartzJob
* [#160](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issue/160) Dynamic Extensions 1.7.1 fails to load on Alfresco 4.2.4


## [1.7.1] - 2017-10-16
### YANKED
* reverted `#145 Add lock support to scheduled jobs` due to NullPointerException that breaks DE.


## [1.7.0] - 2017-10-06 [YANKED]
### New features
* [#147](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/147) Add support for @ResponseBody annotation in webscript
* [#148](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/148) Add support for @RequestBody annotation in webscript

### Improvements
* [#145](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/145) Add lock support to scheduled jobs

### Bug fixes
* [#143](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/143) WebScriptUtil.extractHttpServletResponse() not working.
* [#151](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/151) @ResponseBody with void return type causes NullpointerException


