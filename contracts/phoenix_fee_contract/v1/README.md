# Phoenix HodlCoin: Fee Contract - v1.0.0

## Contract
- [ErgoScript](ergoscript/phoenix_v1_hodlcoin_fee.es)

## Documentation

### Description
Contract guarding the fee box of the HodlCoin protocol.

### Box Contents
Tokens
- None

Registers
- None

### Relevant Transactions
1. Fee Distribution Tx
- Inputs: PhoenixFee1, ... , PhoenixFeeM
- DataInputs: None
- Outputs: Dev1PK, Dev2PK, Dev3PK, PhoenixPK, MinerFee
- Context Variables: None

### Compile Time Constants ($)
- $devPercentage: (Long, Long)
- $phoenixPercentage: (Long, Long)
- $minerFee: Long

### Context Variables (@)
- None
