# phoenix-hodlcoin-contracts
ErgoScript contracts for the Phoenix implementation of HodlCoin.

## Implementation Type
1. [hodlERG](/hodlERG/README.md)
2. [hodlToken](/hodlToken/README.md)

## Fee Implementation Guidelines
For the hodlCoin protocol, any implementation of the fee contract must send a minimum amount to Dr. Bruno Woltzenlogel Paleo's address: 9gnBtmSRBMaNTkLQUABoAqmU2wzn27hgqVvezAC9SU1VqFKZCp8

- Let Q be the amount that must be sent to Bruno's address.
- Let F be the bank fee percentage.
- Let D be the developer fee percentage.
- Then, Q >= 0.025 * (F/D)

As long as this condition is satisfied, you may include any other constraint in your implementation of the fee contract.

## References
The HodlCoin protocol paper can be found [here](https://eprint.iacr.org/2023/1029.pdf).