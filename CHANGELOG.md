---
title: Changelog - Dynamic Extensions for Alfresco
date: 4 July 2018
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

## [2.0.0] - UNRELEASED
### Changed
* Build process refactored to build and compile for specific Alfresco versions
* `@ScheduledQuartzJob` deprecated in favour for vendor-neutral `@ScheduledTask`

### Added
* Alfresco 6 support

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


