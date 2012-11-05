App = (exports ? this).App

# -----------------------------------------------------------------------------

App.ApplicationView = Em.View.extend
  templateName: 'application'

# -----------------------------------------------------------------------------

App.DashboardView = Em.View.extend
  templateName: 'dashboard'

# -----------------------------------------------------------------------------

App.RepositoryView = Em.View.extend
  templateName: 'repository'

# -----------------------------------------------------------------------------

App.DictionaryView = Em.View.extend
  templateName: 'dictionary'

# -----------------------------------------------------------------------------

App.ModelIndexView = Em.View.extend
  templateName: 'model-index'

# -----------------------------------------------------------------------------

App.ModelDetailView = Em.View.extend
  templateName: 'model'

# -----------------------------------------------------------------------------

App.ModelNavigationView = Em.View.extend
  templateName: 'model-navigation'
  selection: null

# -----------------------------------------------------------------------------

App.ModelDefinitionView = Em.View.extend
  templateName: 'model-definition'

# -----------------------------------------------------------------------------

App.ClassDefinitionView = Em.View.extend
  templateName: 'class-definition'
    
# -----------------------------------------------------------------------------

App.PropertyDefinitionView = Em.View.extend

# -----------------------------------------------------------------------------

App.SelectionLink = Em.View.extend

  content: null
  selection: null
  tagName: 'li'
  classNameBindings: 'isActive:active'    
  property: null
  
  isActive: (->
    property = @get('property')
    if Em.empty(property)
      @get('selection') == @get('content')
    else
      @get("selection.#{property}") == @get("content.#{property}")
  ).property('content', 'selection', 'property')

# -----------------------------------------------------------------------------

App.DataTypesView = Em.View.extend
  templateName: 'data-types'

# -----------------------------------------------------------------------------

App.NamespacesView = Em.View.extend
  templateName: 'namespaces'

# -----------------------------------------------------------------------------

App.NavigationBar = Em.View.extend

  # Configuration

  templateName: 'navigation-bar'
  
  # State
  
  isDashboardActive: Routing.currentPathMatches(/dashboard/)
    
  isDictionaryActive: Routing.currentPathMatches(/dictionary/)

# -----------------------------------------------------------------------------

App.MandatoryIndication = Em.View.extend

  template: Em.Handlebars.compile('
    {{#if mandatory}}
      {{#if mandatoryEnforced}}
        Enforced
      {{else}}
        Mandatory
      {{/if}} 
    {{else}}
      Optional
    {{/if}}  
  ')
  
  tagName: 'span'

# -----------------------------------------------------------------------------

App.AssociationSide = Em.View.extend
  template: Em.Handlebars.compile('
    <a {{action showClassDefinition class href="true"}}>{{class.name}}</a>
    {{#if source.many}}many, {{/if}}
    {{view App.MandatoryIndication}}
    
  ')
  
  tagName: 'span'

# -----------------------------------------------------------------------------

App.BundlesTable = Em.View.extend
  templateName: 'bundles-table'

# -----------------------------------------------------------------------------

App.JsonIcon = Em.View.extend
  templateName: 'json-icon'
  large: false
  title: "View as JSON"


