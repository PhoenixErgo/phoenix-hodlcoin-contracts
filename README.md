# phoenix-hodlcoin-contracts
ErgoScript contracts for the Phoenix implementation of HodlCoin.

## Contracts
1. [Proxy Contract](/contracts/proxy_contract/README.md)
2. [Bank Contract](/contracts/bank_contract/README.md)
3. [Phoenix Fee Contract](/contracts/phoenix_fee_contract/README.md)

## Transactions
1. Mint Tx
2. Burn Tx
3. Refund Tx
4. Fee Distribution Tx

## Fee Implementation Guidelines
For the hodlCoin protocol, any implementation of the fee contract must send a minimum amount to Dr. Bruno Woltzenlogel Paleo's address: 9gnBtmSRBMaNTkLQUABoAqmU2wzn27hgqVvezAC9SU1VqFKZCp8

- Let Q be the amount that must be sent to Bruno's address.
- Let F be the bank fee percentage.
- Let D be the developer fee percentage.
- Then, Q >= 0.025 * (F/D)

As long as this condition is satisfied, you may include any other constraint in your implementation of the fee contract.

## hodlERG Phoenix Fee Implementation
For the Phoenix Finance implementation of hodlERG, we kept the original fee percentages:

- F = 3%
- D = 10% * F = 0.3%

The fee distribution was computed by creating a linear program out of the desired constraints, the Jupyter Notebook code can be found [here](/contracts/phoenix_fee_contract/v1/docs/phoenix_fee_distribution.pdf). 

The percentage of the dev fee is split in the following way:
- 0.25% -> Bruno
- 0.25% -> Pulsarz
- 0.25% -> Phoenix
- 0.15% -> Kushti
- 0.1% -> Kras

## References
The HodlCoin protocol paper can be found [here](https://eprint.iacr.org/2023/1029.pdf).

## Protocol Implementation Diagrams
![Image](docs/phoenix_hodlerg_protocol_diagrams.png)