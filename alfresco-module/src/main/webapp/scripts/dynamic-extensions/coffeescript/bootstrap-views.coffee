Bootstrap = Em.Namespace.create()

(exports ? this).Bootstrap = Bootstrap

# -----------------------------------------------------------------------------

Bootstrap.Button = Em.View.extend

  template: Em.Handlebars.compile '
    <input
      {{bindAttr type="view.type"}} 
      {{bindAttr value="view.value"}}
      {{bindAttr class="view.classes"}} 
    />
  '
  
  # Configuration

  type: 'button'
  
  value: null
      
  kind: 'default'
  
  # State
  
  classes: (->
    "btn btn-#{@get('kind')}"
  ).property('kind')  

# -----------------------------------------------------------------------------

Bootstrap.TabItem = Em.View.extend
  
  tagName: 'li'
  
  classNameBindings: 'isActive:active',
    
  template: Em.Handlebars.compile '  
    <a data-toggle="tab" href="#">{{view.label}}</a>
  '
  
  label: ( ->
    @get('content.label') || @get('content.name') || @get('content.title')
  ).property('content')
        
  click: ->
    @set('parentView.selection', @get('content'))
    return false
    
  isActive: (-> 
    @get('parentView.selection') == @get('content')
  ).property('parentView.selection')


Bootstrap.Tabs = Em.CollectionView.extend

  tagName: 'ul'
  
  classNames: ['nav', 'nav-tabs']
    
  itemViewClass: Bootstrap.TabItem
  
  selection: null  
  
# -----------------------------------------------------------------------------

