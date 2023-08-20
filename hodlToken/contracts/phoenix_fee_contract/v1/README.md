# Phoenix HodlToken: Fee Contract - v1.0.0

## Contract
- [ErgoScript](./ergoscript/phoenix_v1_hodltoken_fee.es)

## Documentation

### Description
Contract guarding the fee box of the hodlToken protocol.

### Box Contents
Tokens
- 1. (HodlTokenId, HodlTokenFeeAmount)

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
- $brunoNum: Long
- $phoenixNum: Long
- $kushtiNum: Long

### Context Variables (_)
- None
