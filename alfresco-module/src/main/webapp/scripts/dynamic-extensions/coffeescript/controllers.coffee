App = (exports ? this).App

# -----------------------------------------------------------------------------

App.ApplicationController = Em.Controller.extend();

# -----------------------------------------------------------------------------

App.DashboardController = Em.Controller.extend()

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
