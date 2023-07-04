# Phoenix HodlCoin: Bank Contract - v1.0.0

## Contract
- [ErgoScript](ergoscript/phoenix_v1_hodlcoin_bank.es)

## Documentation

### Description
Contract for the bank box of the HodlCoin protocol.

### Box Contents
Tokens
1. (BankSingletonId, 1)
2. (HodlCoinTokenId, HodlCoinTokenAmount)

Registers
- None

### Relevant Transactions
1. Mint Tx
- Inputs: Bank, Proxy
- DataInputs: None
- Outputs: Bank, UserPK, MinerFee, TxOperatorFee
- Context Variables: None
2. Burn Tx
- Inputs: Bank, Proxy
- DataInputs: None
- Outputs: Bank, UserPK, PhoenixFee, MinerFee, TxOperatorFee
- ContextVariables: None

### Compile Time Constants ($)
- $phoenixFeeContractBytes: Coll[Byte]

### Context Variables (@)
- None
