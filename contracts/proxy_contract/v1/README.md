# Phoenix HodlCoin: Proxy Contract - v1.0.0

## Contract
- [ErgoScript](ergoscript/phoenix_v1_hodlcoin_proxy.es)

## Documentation

### Description
Contract guarding the proxy box of the HodlCoin protocol.

### Box Contents
Tokens
- (HodlCoinTokenId, HodlCoinTokenAmount) if burning hodlCoin tokens.

Registers
- R4: SigmaProp     BuyerPK
- R5: Coll[Byte]    BankSingletonTokenId
- R6: Coll[Byte]    HodlCoinTokenId
- R7: Long          MinBoxValue
- R8: Long          MinTxOperatorFee
- R9: Long          MinerFee

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
- None

### Context Variables (@)
- None
