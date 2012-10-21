Management = Em.Namespace.create()
(exports ? this).Management = Management

Management.Info = Em.Object.extend
  symbolicName: null
  version: null
  
Management.Info.reopenClass
  find: ->    
    info = Em.ObjectProxy.create()
    result = $.Deferred()
    App.api.getManagementInfo().done (data) ->
      info.set('content', Management.Info.create(data))
      result.resolve(info)
    result.promise(info)