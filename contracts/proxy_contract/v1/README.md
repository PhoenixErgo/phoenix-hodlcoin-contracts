# Phoenix HodlCoin: Proxy Contract - v1.0.0

## Contract
- [ErgoScript](ergoscript/phoenix_v1_hodlcoin_proxy.es)

## Documentation

### Description
Contract guarding the proxy box of the HodlCoin protocol.

### Box Contents
Tokens
- (HodlCoinTokenId, HodlCoinTokenAmount)

Registers
- R4: SigmaProp     UserPK
- R5: Long          MinBoxValue
- R6: Long          MinerFee
- R7: Coll[Byte]    BankSingletonTokenId

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
- None

### Context Variables (@)
- None
