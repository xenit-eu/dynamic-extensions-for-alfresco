App = (exports ? this).App

App.Router = Em.Router.extend

  root: Em.Route.extend
  
    index: Em.Route.extend    
      route: '/'
      redirectsTo: 'dashboard'
      
    dashboard: Em.Route.extend
      route: '/dashboard'
      connectOutlets: (router) ->
        router.get('applicationController').connectOutlet('dashboard')
        
    dictionary: Em.Route.extend
      route: '/dictionary'
      #initialState: 'models'
      # Models tab
      models: Em.Route.extend
        route: '/models'
        initialState: 'index'
        index: Em.Route.extend
          route: '/'
          connectOutlets: (router) -> 
            router.get('applicationController').connectOutlet('dictionary')
            controller = router.get('dictionaryController')
            controller.set('selection', App.Dictionary.MODELS)
            controller.connectOutlet('modelIndex', Dictionary.ModelDefinition.findAll())
        show: Em.Route.extend
          route: '/:name'
          deserialize: (router, params) ->
            Dictionary.ModelDefinition.findByName(params.name)
          connectOutlets: (router, context) ->
            App.set('title', context.get('name'))
            context.expand().done ->
              router.get('modelDetailController').connectOutlet('navigation', 'modelNavigation', context)
              router.get('modelDetailController').connectOutlet('modelDefinition', context)
              router.set('modelNavigationController.selection', context)
              router.get('applicationController').connectOutlet('modelDetail', context)
          exit: ->
            App.set('title', null)
      # Data types tab
      dataTypes: Em.Route.extend
        route: '/data-types'
        connectOutlets: (router) -> 
          controller = router.get('dictionaryController')
          controller.set('selection', App.Dictionary.DATA_TYPES)
          controller.connectOutlet('dataTypes', Dictionary.DataType.findAll())
          router.get('applicationController').connectOutlet('dictionary')
      # Namespaces tab
      namespaces: Em.Route.extend
        route: '/namespaces'
        connectOutlets: (router) -> 
          controller = router.get('dictionaryController')
          controller.set('selection', App.Dictionary.NAMESPACES)
          controller.connectOutlet('namespaces', Dictionary.Namespace.findAll())
          router.get('applicationController').connectOutlet('dictionary')
      # Class definitions
      classDefinition: Em.Route.extend
        route: '/classes/:name'
        deserialize: (router, params) ->
          Dictionary.ClassDefinition.findByName(params.name)
        connectOutlets: (router, context) ->
          App.set('title', context.get('name'))
          context.expand().done ->
            modelDefinition = Dictionary.ModelDefinition.findByName(context.get('model.name'))
            modelDefinition.done ->
              router.get('modelDetailController').connectOutlet('navigation', 'modelNavigation', modelDefinition)
              router.get('modelDetailController').connectOutlet('classDefinition', context)
              router.set('modelNavigationController.selection', context)
              router.get('applicationController').connectOutlet('modelDetail', context)
        exit: ->
          App.set('title', null)

    loading: Em.State.extend()
        
    gotoDashboard: Em.Route.transitionTo('dashboard')
    gotoDictionary: Em.Route.transitionTo('dictionary.models')
    showModelDefinitions: Em.Route.transitionTo('dictionary.models.index')
    showModelDefinition: Em.Route.transitionTo('dictionary.models.show')
    showClassDefinition: Em.Route.transitionTo('dictionary.classDefinition')
    showDataTypes: Em.Route.transitionTo('dictionary.dataTypes')
    showNamespaces: Em.Route.transitionTo('dictionary.namespaces')
