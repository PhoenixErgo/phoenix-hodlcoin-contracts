# Phoenix HodlERG: Fee Contract - v1.0.0

## Contract
- [ErgoScript](./ergoscript/phoenix_v1_hodlerg_fee.es)

## Documentation

The fee distribution was computed by creating a linear program out of the desired constraints, the Jupyter Notebook code can be found [here](docs/phoenix_fee_distribution.pdf). 

The percentage of the dev fee is split in the following way:
- 25% -> Bruno
- 25% -> Pulsarz
- 25% -> Phoenix
- 15% -> Kushti
- 10% -> Kras

### Description
Contract guarding the fee box of the hodlERG protocol.

### Box Contents
Tokens
- None

Registers
- None

### Relevant Transactions
1. Fee Distribution Tx
- Inputs: PhoenixFee1, ... , PhoenixFeeM
- DataInputs: None
- Outputs: Bruno, Pulsarz, Phoenix, Kushti, Kras, MinerFee
- Context Variables: None

### Compile Time Constants ($)
- $minerFee: Long

### Context Variables (_)
- None
