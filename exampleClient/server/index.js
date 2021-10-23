require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const CubejsServerCore = require('@cubejs-backend/server-core');
const fs = require('fs');
const cors = require('cors');

var https = require('https');
var privateKey = fs.readFileSync('./server.key', 'utf8');
var certificate = fs.readFileSync('./server.crt', 'utf8');

var credentials = { key: privateKey, cert: certificate };

const app = express();
app.use(require('cors')());
app.use(bodyParser.json({ limit: '50mb' }));
const serverCore = CubejsServerCore.create();
serverCore.initApp(app);
const port = process.env.PORT || 5556;

var httpsServer = https.createServer(credentials, app);
httpsServer.listen(port, (err) => {
  if (err) {
    console.error('Fatal error during server start: ');
    console.error(err.stack || err);
  }
  console.log(`? Cube.js server is listening on ${port}`);
});
app.use(cors());
app.post('/saveschema', (req, res) => {
  const path = 'schema/' + req.body.fileName;
  fs.writeFile(path, req.body.content, function (err) {
    if (err) {
      res.json({ message: err });
    }
    res.json({ message: 'Updated Schema!' });
  });
});

const aggregations = ['sum', 'avg', 'min', 'max', 'runningTotal', 'count', 'countDistinct', 'countDistinctApprox'];
const time_aggregations = ['min', 'max'];
const general_aggregations = ['count', 'countDistinct', 'countDistinctApprox'];
app.get('/generateAggregatedMeasures', (req, res) => {
  fs.readdirAsync('./schema').then((filenames) => {
    console.log(filenames);
    var fileData = {
      files: [],
    };
    filenames.forEach((fileName) => {
      if (fileName.endsWith('.js')) {
        console.log('about to read file');
        var data = fs.readFileSync(`./schema/${fileName}`, 'utf8');
        //var measures = getJsonFromString("measures",data);
        var measures = '';
        var dimensions = getJsonFromString('dimensions', data);
        var dimensionKeys = Object.keys(dimensions);
        var measureCount = 0;
        dimensionKeys.forEach((dimension) => {
          aggregations.forEach((aggregation) => {
            if (
              dimensions[dimension].type == 'number' ||
              (dimensions[dimension].type == 'time' && time_aggregations.indexOf(aggregation) > -1) ||
              general_aggregations.indexOf(aggregation) > -1
            ) {
              // converting sql in lower if it has upper case characters (e.g. in case of postgres)
              measures += `\t\t${aggregation}_${dimensions[dimension].sql.replace(/"(.*)"/g, '$1').toLowerCase()}: {\n`;
              measures += `\t\t\tsql: '${dimensions[dimension].sql}',\n`;
              measures += `\t\t\ttype: '${aggregation}'\n`;
              measures += `\t\t},\n`;
              measureCount++;
            }
          });
        });
        // removing trailing comma
        if (measureCount > 0) {
          measures = measures.substring(0, measures.length - 2);
        }
        var appendPoint = findFirstOccurence(data, 'measures');
        var endPoint = findSecondLastOccurence(data);
        var file_content = data.substring(endPoint);
        var file = fs.openSync(`./schema/${fileName}`, 'r+');
        var bufferedText = new Buffer(`\n${measures}` + file_content);
        fs.writeSync(file, bufferedText, 0, bufferedText.length, appendPoint);
        fileData.files.push({
          content: fs.readFileSync(`./schema/${fileName}`, 'utf8'),
          fileName: fileName,
        });
        fs.close(file);
      }
    });
    res.status(200).send(fileData);
  });
});

// make Promise version of fs.readdir()
fs.readdirAsync = function (dirname) {
  return new Promise(function (resolve, reject) {
    fs.readdir(dirname, function (err, filenames) {
      if (err) reject(err);
      else resolve(filenames);
    });
  });
};

// make Promise version of fs.readFile()
fs.readFileAsync = function (filename, enc) {
  return new Promise(function (resolve, reject) {
    fs.readFile(filename, enc, function (err, data) {
      if (err) reject(err);
      else resolve(data);
    });
  });
};

// utility function, return Promise
function getFile(filename) {
  return fs.readFileSync(filename, 'utf8');
}

function getJsonFromString(startStr, str) {
  var startIndex = str.indexOf(startStr);
  var newStr = '';
  startIndex = startIndex + startStr.length + 2;
  var count = 0;
  var i = 0;
  do {
    newStr = newStr + str[startIndex + i];
    if (str[startIndex + i] == '{') {
      count++;
    } else if (str[startIndex + i] == '}') {
      count--;
    }
    i++;
  } while (count > 0);

  if (startStr == 'measures') {
    var drillMembersStartIndex = newStr.indexOf('drillMembers');
    var drillMembersLastIndex = 0;
    count = 1;
    i = 0;
    if (drillMembersLastIndex > -1) {
      drillMembersStartIndex = drillMembersStartIndex + 15;
      drillMembersLastIndex = drillMembersStartIndex;
      while (count > 0) {
        if (newStr[drillMembersStartIndex + i] != ']') {
          drillMembersLastIndex++;
        } else if (newStr[drillMembersStartIndex + i] == ']') {
          count--;
        }
        i++;
      }
      newStr = newStr.replace(newStr.substring(drillMembersStartIndex, drillMembersLastIndex), '');
    }
  }
  newStr = newStr.replace(/\${CUBE}./g, '');
  newStr = eval('(' + newStr + ')');
  return newStr;
}

function findFirstOccurence(str, data) {
  var measureIndex = str.indexOf(data);
  measureIndex = measureIndex + data.length + 3;
  return measureIndex;
}

function findSecondLastOccurence(data) {
  var measureIndex = data.indexOf('measures');
  var startIndex = measureIndex + 10;
  var secondLastOccurence = 0;
  var count = 1;
  var i = 1;
  while (count > 0) {
    if (data[startIndex + i] == '{') {
      count++;
    } else if (data[startIndex + i] == '}') {
      count--;
    }
    if (data[startIndex + i] == '}' && count == 1) {
      secondLastOccurence = startIndex + i;
    }
    i++;
  }
  return secondLastOccurence + 1;
}
