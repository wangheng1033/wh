const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 8080;
const BACKEND_HOST = 'localhost';
const BACKEND_PORT = 8081;
const STATIC_PATH = path.join(__dirname, 'vhr-project', 'vhr', 'vhr', 'vhrserver', 'vhr-web', 'src', 'main', 'resources', 'static');

const mimeTypes = {
  '.html': 'text/html',
  '.js': 'text/javascript',
  '.css': 'text/css',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.eot': 'application/vnd.ms-fontobject',
  '.ico': 'image/x-icon'
};

function proxyRequest(req, res) {
  const options = {
    hostname: BACKEND_HOST,
    port: BACKEND_PORT,
    path: req.url,
    method: req.method,
    headers: req.headers
  };
  
  const proxy = http.request(options, (backendRes) => {
    res.writeHead(backendRes.statusCode, backendRes.headers);
    backendRes.pipe(res);
  });
  
  proxy.on('error', (err) => {
    res.writeHead(503);
    res.end(JSON.stringify({ status: 503, msg: '后端服务未启动' }));
  });
  
  req.pipe(proxy);
}

const server = http.createServer((req, res) => {
  console.log(`${req.method} ${req.url}`);
  
  if (req.url.startsWith('/verifyCode') || 
      req.url.startsWith('/doLogin') || 
      req.url.startsWith('/logout') ||
      req.url.startsWith('/system/') ||
      req.url.startsWith('/recruitment/') ||
      req.url.startsWith('/basic/') ||
      req.url.startsWith('/personnel/') ||
      req.url.startsWith('/salary/') ||
      req.url.startsWith('/statistics/') ||
      req.url.startsWith('/employee/') ||
      req.method === 'POST') {
    proxyRequest(req, res);
    return;
  }
  
  let filePath = path.join(STATIC_PATH, req.url === '/' ? 'index.html' : req.url);
  
  const extname = path.extname(filePath);
  const contentType = mimeTypes[extname] || 'application/octet-stream';

  fs.readFile(filePath, (error, content) => {
    if (error) {
      if(error.code === 'ENOENT') {
        fs.readFile(path.join(STATIC_PATH, 'index.html'), (error, content) => {
          res.writeHead(200, { 'Content-Type': 'text/html' });
          res.end(content, 'utf-8');
        });
      } else {
        res.writeHead(500);
        res.end('Server Error: '+error.code);
      }
    } else {
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content, 'utf-8');
    }
  });
});

server.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}/`);
  console.log(`API proxy to: http://${BACKEND_HOST}:${BACKEND_PORT}`);
  console.log(`VerifyCode: http://localhost:${PORT}/verifyCode`);
});

