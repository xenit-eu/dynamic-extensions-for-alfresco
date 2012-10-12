#!/usr/bin/env ruby
# Watchr script that monitors changes to files related to the Dynamic Extensions console.
# and deploys them to a live Alfresco development environment.

require 'watchr'

alfresco_home = ARGV[1] || ENV['DYNAMIC_EXTENSIONS_ALFRESCO_HOME'] || ENV['ALFRESCO_HOME'] || ENV['ALF_HOME']
raise 'Could not determine Alfresco home.' unless alfresco_home

puts "Watching for source file changes."
puts "Changes will be deployed to '#{alfresco_home}'"

alfresco_webapp = "#{alfresco_home}/tomcat/webapps/alfresco"
alfresco_webapp_classpath = "#{alfresco_home}/tomcat/webapps/alfresco/WEB-INF/classes"

puts "Watching Web Script source files."
webscripts_deploy = "#{alfresco_webapp_classpath}/alfresco/extension/templates/webscripts"
watch "src/main/webscripts/(.*\.(xml|ftl|html))" do |md|
  puts "Deploying Web Script file #{md}"
  FileUtils.cp md[0], "#{webscripts_deploy}/#{md[1]}"
end

puts "Watching CSS source files."
css_deploy = "#{alfresco_webapp}/css"
watch "src/main/webapp/css/(.*\.css)" do |md|
  puts "Deploying CSS file #{md}"
  FileUtils.cp md[0], "#{css_deploy}/#{md[1]}"
end

puts "Watching CoffeeScript source files."
javascript_deploy = "#{alfresco_webapp}/scripts"
scripts = "src/main/webapp/scripts"
watch "#{scripts}/dynamic-extensions/coffeescript/(.*?)\.coffee" do |md|
  puts "Compiling CoffeeScript file #{md}"
  system("coffee -c -o #{scripts}/dynamic-extensions/javascript #{md[0]}")
  javascript = "#{scripts}/dynamic-extensions/javascript/#{md[1]}.js"
  puts "Deploying JavaScript file #{javascript}"
  FileUtils.cp javascript, "#{javascript_deploy}/dynamic-extensions/javascript"
end

# Handles Ctrl+C
Signal.trap('INT') { abort('\n') }