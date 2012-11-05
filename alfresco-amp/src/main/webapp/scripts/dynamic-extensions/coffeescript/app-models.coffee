App = (exports ? this).App

# -----------------------------------------------------------------------------

App.Option = Em.Object.extend
  value: null
  label: null

# -----------------------------------------------------------------------------

App.SortableArrayProxy = Em.ArrayProxy.extend Em.SortableMixin, {}

# -----------------------------------------------------------------------------

App.Dictionary = Em.Object.extend()
App.Dictionary.reopenClass(
  MODELS: App.Option.create({ label: 'Models', value: 'showModelDefinitions'})
  DATA_TYPES: App.Option.create({ label: 'Data Types', value: 'showDataTypes'})
  NAMESPACES: App.Option.create({ label: 'Namespaces', value: 'showNamespaces'})
)
