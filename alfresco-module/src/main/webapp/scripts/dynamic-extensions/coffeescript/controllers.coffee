App = (exports ? this).App

# -----------------------------------------------------------------------------

App.ApplicationController = Em.Controller.extend()

# -----------------------------------------------------------------------------

App.DashboardController = Em.Controller.extend
  updateContent: ->
    info = Management.Info.find()
    info.done =>
      @set('content', info)
      
  extensionBundles: (->
    this._filterExtensions({isCoreBundle: false})
  ).property('content')

  coreBundles: (->
    this._filterExtensions({isCoreBundle: true})
  ).property('content')

  _filterExtensions: (options) ->
    extensions = @get('content.extensions')
    if extensions
      @get('content.extensions').filter (extension) ->
        extension.isCoreBundle == options.isCoreBundle
    else 
      [ ]
  

# -----------------------------------------------------------------------------

App.RepositoryController = Em.Controller.extend()

# -----------------------------------------------------------------------------

App.DictionaryController = Em.Controller.extend

  content: null
  selection: null

  init: ->
    @set('content', [ App.Dictionary.MODELS, App.Dictionary.NAMESPACES, App.Dictionary.DATA_TYPES ])
    
  selectionDidChange: (->
    App.router.send(@get('selection.value'))    
  ).observes('selection')

# -----------------------------------------------------------------------------

App.ModelIndexController = Em.ArrayController.extend
  sortProperties: ['name']
  
# -----------------------------------------------------------------------------

App.ModelDetailController = Em.Controller.extend()
  
# -----------------------------------------------------------------------------

App.ModelNavigationController = Em.Controller.extend
  selection: null  

# -----------------------------------------------------------------------------

App.ModelDefinitionController = Em.Controller.extend()

# -----------------------------------------------------------------------------

App.ClassDefinitionController = Em.Controller.extend()
  
# -----------------------------------------------------------------------------

App.ModelTypeController = Em.Controller.extend()

# -----------------------------------------------------------------------------

App.ModelAspectController = Em.Controller.extend()

# -----------------------------------------------------------------------------

App.DataTypesController = Em.ArrayController.extend
  sortProperties: ['name']

# -----------------------------------------------------------------------------

App.NamespacesController = Em.ArrayController.extend
  sortProperties: ['uri']
