Dictionary = Em.Namespace.create()
(exports ? this).Dictionary = Dictionary

# -----------------------------------------------------------------------------

Dictionary.ModelDefinition = Em.Object.extend Util.Expandable,
  _doExpand: ->
    Dictionary.ModelDefinition.findByName(this.get('name'), this)
    
  published: (->
    value = @get('publishedDate')    
    new Date(value).toDateString() if value
  ).property('publishedDate')

Dictionary.ModelDefinition.reopenClass
  findAll: ->
    modelDefinitions = [ ]
    result = $.Deferred()
    App.api.getModelDefinitions().done (data) -> 
      modelDefinitions.addObjects(data.map (item) -> 
        modelDefinition = Dictionary.ModelDefinition.create(item)        
      )
      result.resolve(modelDefinitions)
    result.promise(modelDefinitions)
  findByName: (name, modelDefinition) ->
    unless modelDefinition
      modelDefinition = Dictionary.ModelDefinition.create({ name: name })
    modelDefinition.set('_expanded', true)
    result = $.Deferred()
    App.api.getModelMetadata(name).done (data) ->
      data.types = data.types.map (item) -> Dictionary.ClassDefinition.create(item)
      data.aspects = data.aspects.map (item) -> Dictionary.ClassDefinition.create(item)
      modelDefinition.setProperties(data)
      result.resolve(modelDefinition)
    result.promise(modelDefinition)

# -----------------------------------------------------------------------------

Dictionary.ClassDefinition = Em.Object.extend Util.Expandable,
  _doExpand: ->
    Dictionary.ClassDefinition.findByName(this.get('name'), this)

Dictionary.ClassDefinition.reopenClass
  findByName: (name, classDefinition) ->   
    unless classDefinition
      classDefinition = Dictionary.ClassDefinition.create({ name: name })
    classDefinition.set('_expanded', true)
    result = $.Deferred()
    App.api.getClassDefinition(name).done (data) ->
      if data.parent
        data.parent = Dictionary.ClassDefinition.create(data.parent)
      if data.children
        data.children = data.children.map (child) -> Dictionary.ClassDefinition.create(child)
      data.defaultAspects = data.defaultAspects.map (aspect) -> Dictionary.ClassDefinition.create(aspect)
      data.model = Dictionary.ModelDefinition.create({ name: data.model })
      data.associations.forEach (association) -> 
        association.source.class = Dictionary.ClassDefinition.create(association.source.class)
        association.target.class = Dictionary.ClassDefinition.create(association.target.class)
      if data.properties
        data.properties.forEach (property) ->
          if property.containerClass
            property.containerClass = Dictionary.ClassDefinition.create(property.containerClass)
      classDefinition.setProperties(data)
      result.resolve(classDefinition)
    result.promise(classDefinition)
  
# -----------------------------------------------------------------------------

Dictionary.DataType = Em.Object.extend()

Dictionary.DataType.reopenClass
  findAll: ->
    result = $.Deferred()
    dataTypes = [ ]
    App.api.getDataTypes().done (data) ->
      dataTypes.addObjects(data.map (item) -> Dictionary.DataType.create(item))
      result.resolve(dataTypes)
    result.promise(dataTypes)

# -----------------------------------------------------------------------------

Dictionary.Namespace = Em.Object.extend()

Dictionary.Namespace.reopenClass
  findAll: ->
    result = $.Deferred()
    namespaces = [ ]
    App.api.getNamespaces().done (data) ->
      data = data.filter (item) -> not Em.empty(item.uri)
      data.map (item) -> Dictionary.Namespace.create(item)
      namespaces.addObjects(data.map (item) -> Dictionary.Namespace.create(item))
      result.resolve(namespaces)
    result.promise(namespaces)
  