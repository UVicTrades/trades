# UVic Trades

```mermaid
openapi: 3.1.0
info:
  title: UVicTrades
  version: 1.0.0
  license:
    name: AGPLv3
    url: https://www.gnu.org/licenses/agpl-3.0.en.html
servers:
  - url: http://localhost/
components:
  securitySchemes:
    jwt:
      type: http
      scheme: bearer
      bearerFormat: JWT
paths:
  /authentication/login:
    post:
      tags: [User]
      summary: login
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                user_name: test
                password: test
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  token: EySAuaASioASDh...
  /authentication/register:
    post:
      tags: [User]
      summary: register
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                user_name: test
                password: test
                name: Test User
      responses:
        '201':
          description: Created
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data: null
  /transaction/getStockPrices:
    get:
      tags: [Stock]
      security:
        - jwt: []
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  - stock_id: 1
                    stock_name: Apple
                    current_price: 100
                  - stock_id: 2
                    stock_name: Google
                    current_price: 200
  /transaction/getStockPortfolio:
    get:
      tags: [Stock]
      summary: getStockPortfolio
      security:
        - jwt: []
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  - stock_id: 1
                    stock_name: Apple
                    quantity_owned: 100
                  - stock_id: 2
                    stock_name: Google
                    quantity_owned: 150
  /transaction/getWalletBalance:
    get:
      tags: [Stock]
      summary: getWalletBalance
      security:
        - jwt: []
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  balance: 100
  /transaction/getWalletTransactions:
    get:
      tags: [Stock]
      summary: getWalletTransactions
      security:
        - jwt: []
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  - wallet_tx_id: 628ba23df2210df6c3764823
                    stock_tx_id: 62738363a50350b1fbb243a6
                    is_debit: true
                    amount: 100
                    time_stamp: '2024-01-12T15:03:25.019+00:00'
                  - wallet_tx_id: 628ba36cf2210df6c3764824
                    stock_tx_id: 62738363a50350b1fbb243a6
                    is_debit: false
                    amount: 200
                    time_stamp: '2024-01-12T14:13:25.019+00:00'
  /transaction/getStockTransactions:
    get:
      tags: [Stock]
      summary: getStockTransactions
      security:
        - jwt: []
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  - stock_tx_id: 62738363a50350b1fbb243a6
                    stock_id: 1
                    wallet_tx_id: 628ba23df2210df6c3764823
                    order_status: COMPLETED
                    is_buy: true
                    order_type: LIMIT
                    stock_price: 50
                    quantity: 2
                    time_stamp: '2024-01-12T15:03:25.019+00:00'
                  - stock_tx_id: 62738363a50350b1fbb243a6
                    stock_id: 1
                    wallet_tx_id: 628ba36cf2210df6c3764824
                    order_status: COMPLETED
                    is_buy: false
                    order_type: MARKET
                    stock_price: 100
                    quantity: 2
                    time_stamp: '2024-01-12T14:13:25.019+00:00'
  /engine/placeStockOrder:
    post:
      tags: [Algorithm]
      summary: placeStockOrder
      security:
        - jwt: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                stock_id: ad67ba79-e7ed-480e-9939-df5ca679367a
                is_buy: false
                order_type: LIMIT
                quantity: 10
                price: 80
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              examples:
                example-0:
                  summary: placeStockOrder-buyLimit
                  value:
                    success: true
                    data: null
                example-1:
                  summary: placeStockOrder-sellLimit
                  value:
                    success: true
                    data: null
                example-2:
                  summary: placeStockOrder-buyMarket
                  value:
                    success: true
                    data: null
                example-3:
                  summary: placeStockOrder-sellMarket
                  value:
                    success: true
                    data: null
  /engine/cancelStockTransaction:
    post:
      tags: [Algorithm]
      summary: cancelStockTransaction
      security:
        - jwt: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                stock_tx_id: 62738363a50350b1fbb243a6
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data: null
  /transaction/addMoney:
    post:
      tags: [Admin]
      summary: addMoneyToWallet
      security:
        - jwt: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                amount: 100
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data: null
  /setup/addStockToUser:
    post:
      tags: [Admin]
      summary: addStockToUser
      security:
        - jwt: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              example:
                stock_id: ad67ba79-e7ed-480e-9939-df5ca679367a
                quantity: 100
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                examples: [application/json]
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data: null
  /setup/createStock:
    post:
      tags: [Admin]
      summary: createStock
      security:
        - jwt: []
      requestBody:
        content:
          application/json:
            schema:
              type: object
              examples: [stock_name: Apple]
      responses:
        '200':
          description: OK
          headers:
            Content-Type:
              schema:
                type: string
                example: application/json
          content:
            application/json:
              schema:
                type: object
              example:
                success: true
                data:
                  stock_id: your_stock_id
```
