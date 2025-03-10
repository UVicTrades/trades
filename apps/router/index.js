const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
app.use(express.json());

const backends = {
  '1': 'http://trade_backend1:8200',
  '2': 'http://trade_backend2:8200'
};

app.post('/placeStockOrder', (req, res, next) => {
  const { stock_id } = req.body;
  if (!stock_id) {
    return res.status(400).json({ error: 'Missing stock id in request data.' });
  }
  
  const target = backends[parseInt(stock_id)];
  if (!target) {
    return res.status(400).json({ error: 'Invalid stock id. Allowed values: 1 or 2.' });
  }
  
  console.log(`Routing stock order with id ${id} to backend ${target}`);
  
  createProxyMiddleware({
    target,
    changeOrigin: true,
  })(req, res, next);
});

app.use((req, res) => {
  res.status(404).json({ error: 'Not Found' });
});

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`Custom router listening on port ${port}`));
