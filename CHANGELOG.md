---
title: Changelog - Dynamic Extensions for Alfresco
date: 10 May 2021
report: true
colorlinks: true
---
<!--
Changelog for DE

See http://keepachangelog.com/en as reference
Version template:

## [X.X.X] - yyyy-MM-dd
### Added (for new features)
### Changed (for changes in existing functionality)
### Deprecated (for soon-to-be removed features)
### Removed (for now removed features)
### Fixed (for any bug fixes)
### Security (in case of vulnerabilities)
### YANKED (for reverted functionality in)
 -->
# Dynamic Extensions For Alfresco Changelog

## [2.1.1] - UNRELEASED
### Fixed
* [#332](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/332) DEVEM-486 Fix regresssion in JSONWriterResolution

## [2.1.0] - 2021-05-10
### Added
* Alfresco 7 support
* [#321](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/321) Gradle plugin supports creating DE Bundles from arbitrary Jars

## [2.0.5] - 2020-09-10
### Fixed
* [#306](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/306) Alfresco 6.2.1 issues due to cglib imports in control-panel bundle
* [#308](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/308) Calling webscript without required `@RequestParam` causes 500 error response

## [2.0.4] - 2020-05-27
### Fixed
* [#297](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/297) Multiple bean candidates for a Quartz' `SchedulerFactoryBean`
* [#201](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/201) Fixed issue where Alfresco would not start when Dynamic-extensions and Records Management are installed at the same time


## [2.0.3] - 2020-03-12
### Added
* Alfresco 6.2 compatible AMP artifact

### Fixed
* [#287](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/287) Dynamic Extensions not starting with Spring 3 and no internet
* [#284](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/284) Fixes `AnnotationBasedActionExecuter` throws `ClassCastException`
* [#281](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/281) Invalid system packages (WEB-INF/lib) cache state in a docker based setup
* [#275](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/275) Registering behaviours for model that are deployed in the same extension fail
* [#197](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/197) Deploying dependent document models from the same bundle are not loaded in the correct order
* [#265](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/265) `DeBundle` task failing to use build cache


## [2.0.2] - 2019-10-08
### Fixed
 * [#267](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/267) Dynamic Extensions not starting with Spring 5 and no internet

## [2.0.1] - 2019-05-21
### Fixed
 * [#258](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/258) Dynamic Extensions Gradle plugin fails with `java.lang.NoClassDefFoundError: aQute/bnd/osgi/Builder`

## [2.0.0] - 2019-05-20
### Added
* Alfresco 6 support

### Changed
* Build process refactored to build and compile for specific Alfresco versions
* `@ScheduledQuartzJob` deprecated in favour for vendor-neutral `@ScheduledTask`
* Gradle plugin refactored
  * Only adds dynamic extensions dependencies to `compileOnly`, and this can be turned off
  * Removed `CallWebScript` task type
  * The `jar` task now used bnd to build OSGi bundles
  * Added `DeBundle` task type that can be used to manually create dynamic extension bundles

### Fixed
* [#189](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/189)
Registering a component implementing multiple Activiti listener Interfaces throws exception on startup

## [1.7.6] - 2018-09-05
* [#189](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/189) Support Json 2 on Alfresco 4

## [1.7.5] - 2018-07-04
### Fixed
* [#180](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/180) Use compileOnly dependencies in the gradle plugin
* [#181](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/181) Fixed regression for static resources embedded in DE bundle

## [1.7.4] - 2018-03-08
### Added
* [#173](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issue/173) Added CHANGELOG.md to the project

### Security
* [#176](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/176) Fix Reflected XSS through maliciously formed URLs

## [1.7.3] - 2017-12-22
### Added
* [#167](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/167) Make WebScripts work with HttpEntity return value
* [#169](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/169) Add an issue and pull request template


## [1.7.2] - 2017-12-11
### Fixed
* [#156](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/156) NullPointerException in GenericQuartzJob
* [#160](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/160) Dynamic Extensions 1.7.1 fails to load on Alfresco 4.2.4


## [1.7.1] - 2017-10-16
### YANKED
* reverted `#145 Add lock support to scheduled jobs` due to NullPointerException that breaks DE.


## [1.7.0] - 2017-10-06 [YANKED]
### Added
* [#147](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/147) Add support for @ResponseBody annotation in webscript
* [#148](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/148) Add support for @RequestBody annotation in webscript

### Changed
* [#145](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/pull/145) Add lock support to scheduled jobs

### Fixed
* [#143](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/143) WebScriptUtil.extractHttpServletResponse() not working.
* [#151](https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/151) @ResponseBody with void return type causes NullpointerException

