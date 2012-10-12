Util = Em.Namespace.create()
(exports ? this).Util = Util

###
  Mixin for enabling an object to expand itself.
###
Util.Expandable = Em.Mixin.create
  _expanded: false
  
  ###
    Expands this object. Does nothing if this object is already expanded.
    
    @params {any} Any arguments are passed to _doExpand()
    @returns {Object} this object, with a Promise to track completion.
  ###
  expand: ->
    unless this.get('_expanded')
      this._doExpand.apply(this, arguments)
    else
      $.Deferred().resolve(this).promise(this)
  
  ###
    Internal worker function that performs the actual expansion of this object.
    
    @params {any} The arguments passed to expand()
  ###
  _doExpand: Em.required(Function)
  

Routing = Em.Namespace.create()
(exports ? this).Routing = Routing

###
  Declares a property that tests if the Router's current path matches a given path.
  
  This property is bound to 'App.router.currentPath'.
  
  @param {RegExp} path
    A regular expression for matching against the current path.
  @returns {Boolean} True if the path matches, false if not.
###
Routing.currentPathMatches = (path) ->
  (->
    App.get('router.currentPath')?.match(path)    
  ).property('App.router.currentPath')

