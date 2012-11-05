App = Em.Application.create

  rootElement: '#application'
  
  autoinit: true
  
  applicationTitle: 'Dynamic Extensions for Alfresco'
  
  title: ''  
  
  titleDidChange: (->
    title = @get('title')
    if title
      title = "#{title} - #{@get('applicationTitle')}"
    else
      title = @get('applicationTitle')
    document.title = title
  ).observes('title', 'applicationTitle')

App.Api = Em.Object.extend

  # Configuration
  
  baseUri: 'http://localhost:8080/alfresco/service'
  
  managementInfoUri: (->
    @_uri('/dynamic-extensions/management/info')
  ).property('baseUri')
  
  dictionaryUri: (->
    @_uri('/json-api/dictionary')
  ).property('baseUri')

  dataTypesUri: (->
    @_uri('/json-api/dictionary/data-types')
  ).property('baseUri')
  
  namespacesUri: (->
    @_uri('/json-api/dictionary/namespaces')
  ).property('baseUri')

  _uri: (relativePath) ->
    @get('baseUri') + relativePath
    
  # Initialization
  
  init: ->
    $.ajaxSetup(
      dataFilter: (data, type) ->
        data.replace(/"\/Date\((\d+)\)\/"/, '$1')
    )
        
  # Main operations
  
  getManagementInfo: ->
    location = @get('managementInfoUri')
    $.getJSON(location).promise()
    
  getModelDefinitions: ->
    location = "#{@get('dictionaryUri')}/models"
    $.getJSON(location).promise()
      
  getModelMetadata: (name) ->
    location = "#{@get('dictionaryUri')}/models/#{encodeURI(name)}/metadata"
    $.getJSON(location).promise()
  
  getClassDefinition: (name) ->
    location = "#{@get('dictionaryUri')}/classes/#{encodeURI(name)}"
    $.getJSON(location).promise()    
  
  getDataTypes: ->
    location = @get('dataTypesUri')
    $.getJSON(location).promise()
      
  getNamespaces: ->
    location = @get('namespacesUri')
    $.getJSON(location).promise()
      
App.api = App.Api.create()


(exports ? this).App = App
