const express = require("express");
const exec = require("child_process").exec;
const path = require("path");
const app = express();
const bodyParser = require("body-parser");

app.use(bodyParser.urlencoded({ extended: true }));

app.use(express.static(path.join(__dirname, "./../client/")));

const PORT = 2000;

app.listen(PORT, () => {
  console.log(`listen Port ${PORT}`);
});

app.post("/download-repo", (req, res) => {
  const n = req.body.repo;
  downloadRepo(n);
});

app.post("/generate-cv", (req, res) => {
  const n = req.body.gcv;
  generateCV(n);
});

app.post("/photomosaic", (req, res) => {
  const obj = req.body;
  generatePhotomosaic(obj);
});

function generatePhotomosaic(info) {
  const { image, mosaicX, mosaicY, imageX, imageY, gcv } = info;
  let { userepo0, userepo1, userepo2, userepo3 } = info;

  if (typeof userepo0 == "undefined") userepo0 = "false";
  if (typeof userepo1 == "undefined") userepo1 = "false";
  if (typeof userepo2 == "undefined") userepo2 = "false";
  if (typeof userepo3 == "undefined") userepo3 = "false";

  const prompt = `java -jar ./../../Photomosaic.jar --web mosaic ${image} ${userepo0} ${userepo1} ${userepo2} ${userepo3} ${mosaicX} ${mosaicY} ${imageX} ${imageY} ${gcv}`;

  child = exec(prompt, function (error, stdout, stderr) {
    console.log("stdout: " + stdout);
    console.log("stderr: " + stderr);
    if (error !== null) {
      console.log("exec error: " + error);
    }
  });
}

function generateCV(n) {
  child = exec(
    `java -jar ./../../Photomosaic.jar --web vector ${n}`,
    function (error, stdout, stderr) {
      console.log("stdout: " + stdout);
      console.log("stderr: " + stderr);
      if (error !== null) {
        console.log("exec error: " + error);
      }
    }
  );
}

function downloadRepo(n) {
  child = exec(
    `java -jar ./../../Photomosaic.jar --web download ${n}`,
    function (error, stdout, stderr) {
      console.log("stdout: " + stdout);
      console.log("stderr: " + stderr);
      if (error !== null) {
        console.log("exec error: " + error);
      }
    }
  );
}
