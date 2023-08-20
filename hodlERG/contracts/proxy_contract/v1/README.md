# Phoenix HodlERG: Proxy Contract - v1.0.0

## Contract
- [ErgoScript](./ergoscript/phoenix_v1_hodlerg_proxy.es)

## Documentation

### Description
Contract guarding the proxy box of the hodlERG protocol.

### Box Contents
Tokens
- (HodlERGTokenId, HodlERGTokenAmount) if burning hodlERG tokens.

Registers
- R4: SigmaProp     BuyerPK
- R5: Coll[Byte]    BankSingletonTokenId
- R6: Coll[Byte]    HodlERGTokenId
- R7: Long          MinBoxValue
- R8: Long          MinerFee
- R9: Long          TxOperatorFee

### Relevant Transactions
1. Mint Tx
- Inputs: Bank, Proxy
- Data Inputs: None
- Outputs: Bank, BuyerPK, MinerFee, TxOperatorFee
- Context Variables: None
2. Burn Tx
- Inputs: Bank, Proxy
- Data Inputs: None
- Outputs: Bank, BuyerPK, PhoenixFee, MinerFee, TxOperatorFee
- Context Variables: None
3. Refund Tx
- Inputs: Proxy
- Data Inputs: None
- Outputs: BuyerPK, MinerFee
- Context Variables: None

### Compile Time Constants ($)
- $minTxOperatorFee: Long

### Context Variables (_)
- None
