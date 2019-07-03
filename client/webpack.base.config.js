const ScalaJS = require('./scalajs.webpack.config');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');
const rootDir = path.resolve(__dirname, '../../../../src/main/resources');
const cssDir = path.resolve(rootDir, 'css');
const vendorsDir = path.resolve(rootDir, 'vendors');

const WebApp = Merge(ScalaJS, {
  entry: {
    styles: [path.resolve(cssDir, './pimpweb.js')],
    vendors: [path.resolve(vendorsDir, './vendors.js')],
    fonts: [path.resolve(cssDir, './fonts.js')]
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          { loader: 'css-loader', options: { importLoaders: 1, url: true } },
          'postcss-loader'
        ]
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        use: [
          { loader: 'file-loader', options: { name: 'assets/static/fonts/[name].[hash].[ext]' } }
        ],
        include: /node_modules/
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        use: [
          { loader: 'url-loader', options: { limit: 100000, name: 'static2/fonts/[name]-[hash].[ext]' } }
        ],
        exclude: /node_modules/
      },
      {
        test: /\.less$/,
        use: [
          MiniCssExtractPlugin.loader,
          { loader: 'css-loader', options: { importLoaders: 1 } },
          'postcss-loader',
          'less-loader'
        ]
      }
    ]
  },
  output: {
    filename: '[name].[chunkhash].js',
  },
  plugins: [
    new MiniCssExtractPlugin({filename: '[name].[contenthash].css'})
  ]
});

module.exports = WebApp;
