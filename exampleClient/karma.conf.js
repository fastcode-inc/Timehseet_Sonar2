// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage-istanbul-reporter'),
      require('@angular-devkit/build-angular/plugins/karma'),
      // require('karma-scss-preprocessor')
    ],
    files: [
      { pattern: '../src/test.ts', watched: false },
      // { pattern: '../src/styles/base.scss', watched: true,  included: true, served: true },
      // { pattern: '../src/styles/styles.scss', watched: true,  included: true, served: true },
      // { pattern: '../src/styles/theme-main.scss', watched: true,  included: true, served: true },
      // {pattern: '../node_modules/@angular/material/_theming.scss', included: true, watched: true},
      // {
      //   pattern: '**/*.scss',
      //   watched: true,
      //   included: true,
      //   served: true
      // } ,
      {pattern: './node_modules/@angular/**/*', included: false, watched: false},
      {pattern: './node_modules/rxjs/**/*', included: false, watched: false},
       // Include a Material theme in the test suite.
      //  {pattern: '../dist/packages/**/core/theming/prebuilt/indigo-pink.css', included: true, watched: true},

       // Includes all package tests and source files into karma. Those files will be watched.
       // This pattern also matches all sourcemap files and TypeScript files for debugging.
       {pattern: './dist/packages/**/*', included: false, watched: true},
                 
    ],
    // preprocessors: {
    //   '../node_modules/@angular/material/_theming.scss':['scss'],
    //   '**/*.scss': ['scss']      
      
    // },
    // scssPreprocessor: {
    //   options: {
    //     sourceMap: true,
    //     includePaths: ['bower_components']
    //   }
    // },
    client: {
      clearContext: false, // leave Jasmine Spec Runner output visible in browser
      jasmine: {
        timeoutInterval: 10000
      }
    },
    coverageIstanbulReporter: {
      dir: require('path').join(__dirname, './coverage'),
      reports: ['html', 'lcovonly'],
      fixWebpackSourcePaths: true
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false
  });
};