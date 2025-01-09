# UVic Trades

## Local Development

> [!NOTE]
> **BEFORE** you run the following steps make sure you have [`docker`](https://docs.docker.com/engine/install/) & the [`docker compose`](https://docs.docker.com/compose/install/#scenario-two-install-the-compose-plugin) plugin installed and running

```shell
# Clone the repository
git clone https://github.com/UVicTrades/trades && cd trades

# Run the docker compose stack with hot reloading
docker compose up --build --wait

# View all container logs
docker compose logs -f

# When you want to shut it down
docker compose down -v
```

The development environment is now running (give it ~1min to spin up) and accessible at [http://localhost/](http://localhost/)

## System Architecture

```mermaid
flowchart TD
    subgraph frontend_rect["Frontend"]
        CB[Client Browser]
    end

    P{{Proxy}}
    CB -->|HTTP /| P

    subgraph backend_rect["Backend"]
    direction LR
        OS(Order Service)
        US(User Service)
        MS(Market Service)
        AS(Admin Service)
    end
    P -->|HTTP /authentication/*| US
    P -->|HTTP /engine/*| OS
    P -->|HTTP /transaction/get*| MS
    P -->|HTTP /setup/*| AS

    Q@{ shape: das, label: "Order Queue" }
    OS --> Q

    subgraph algo_rect["Algorithm"]
        M(Matching Engine)
    end

    Q --> M

    subgraph db_rect["Database"]
    direction LR
        C[(Cache)]
        OB[(Orderbook)]
        DB[(Primary)]
        DBR[(Read Replica)]
    end

    US & AS--> DB
    MS --> DBR
    M --> DB & OB


    style db_rect       stroke:#e91e63,stroke-width:3px,fill:none,stroke-dasharray:4,4
    style backend_rect  stroke:#9b59b6,stroke-width:3px,fill:none,stroke-dasharray:4,4
    style algo_rect     stroke:#2ecc71,stroke-width:3px,fill:none,stroke-dasharray:4,4
    style frontend_rect stroke:#3498db,stroke-width:3px,fill:none,stroke-dasharray:4,4
```
