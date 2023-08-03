# phoenix-hodlcoin-contracts: hodlERG
ErgoScript contracts for the Phoenix implementation of hodlERG.

## Contracts
1. [Proxy Contract](./contracts/proxy_contract/README.md)
2. [Bank Contract](./contracts/bank_contract/README.md)
3. [Phoenix Fee Contract](./contracts/phoenix_fee_contract/README.md)

## Transactions
1. Mint Tx
2. Burn Tx
3. Refund Tx
4. Fee Distribution Tx

## hodlERG Phoenix Fee Implementation
For the Phoenix Finance implementation of hodlERG, we kept the original fee percentages:

- F = 3%
- D = 10% * F = 0.3%

The fee distribution was computed by creating a linear program out of the desired constraints, the Jupyter Notebook code can be found [here](./contracts/phoenix_fee_contract/v1/docs/phoenix_fee_distribution.pdf). 

The percentage of the dev fee is split in the following way:
- 0.25% -> Bruno
- 0.25% -> Pulsarz
- 0.25% -> Phoenix
- 0.15% -> Kushti
- 0.1% -> Kras

## Protocol Diagrams
![Image](./docs/phoenix_hodlerg_protocol_diagrams.png)