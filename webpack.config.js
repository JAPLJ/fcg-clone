const webpack = require('webpack');
const PRODUCTION = process.env.BUILD_ENV === 'production';

module.exports = {
  mode: 'production',
  entry: './app/assets/js/application.ts',
  output: {
    filename: (PRODUCTION ? "./app/assets/js/application-all.min.js" : "./app/assets/js/application-all.js"),
    path: __dirname
  },
  resolve: {
    extensions: ['.ts', '.js'],
    alias: {
      'vue$': 'vue/dist/vue.esm.js'
    }
  },
  module: {
    rules: [
      { test: /\.ts$/, use: 'ts-loader' },
      { test: /\.html$/, loader: 'html-loader?minimize=false' }
    ]
  },

  plugins:
    (PRODUCTION ?
      [
        new webpack.DefinePlugin({ PRODUCTION: JSON.stringify(true) }),
        new webpack.DefinePlugin({ 'process.env': { NODE_ENV: JSON.stringify(process.env.BUILD_ENV) } }),
        new webpack.optimize.UglifyJsPlugin(),
        new webpack.optimize.OccurrenceOrderPlugin(),
        new webpack.ProvidePlugin({ $: 'jquery', jQuery: 'jquery', Vue: 'vue' })
      ] :
      [
        new webpack.DefinePlugin({ PRODUCTION: JSON.stringify(true) }),
        new webpack.DefinePlugin({ 'process.env': { NODE_ENV: JSON.stringify(process.env.BUILD_ENV) } }),
        new webpack.ProvidePlugin({ $: 'jquery', jQuery: 'jquery', Vue: 'vue' })
      ]
    ),

  cache: true
};