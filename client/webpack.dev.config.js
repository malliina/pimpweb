const ScalaJS = require('./scalajs.webpack.config');
const Merge = require('webpack-merge');
const path = require('path');
const rootDir = path.resolve(__dirname, '../../../..');
const cssDir = path.resolve(rootDir, 'css');

const WebApp = Merge(ScalaJS, {
  mode: 'development',
  entry: {
    styles: [path.resolve(cssDir, './pimpweb.js')],
    fonts: [path.resolve(cssDir, './fonts.js')]
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          'style-loader',
          { loader: 'css-loader', options: { importLoaders: 1 } },
          'postcss-loader'
        ]
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        loader: 'url-loader?limit=100000'
      },
      {
        test: /\.less$/,
        use: [
          'style-loader',
          { loader: 'css-loader', options: { importLoaders: 1 } },
          'less-loader'
        ]
      }
    ]
  },
  plugins: [

  ]
});

module.exports = WebApp;
