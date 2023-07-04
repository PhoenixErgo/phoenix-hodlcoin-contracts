{

    // ===== Contract Information ===== //
    // Name: Phoenix HodlCoin Proxy
    // Description: Contract guarding the proxy box for the HodlCoin protocol.
    // Version: 1.0.0
    // Author: Luca D'Angelo (ldgaetano@protonmail.com), MGPai

    // ===== Box Contents ===== //
    // Tokens
    // 1. (HodlCoinTokenId, HodlCoinTokenAmount)
    // Registers
    // R4: SigmaProp    UserPK
    // R5: Long         MinBoxValue
    // R6: Long         MinerFee
    // R7: Long         MinTxOperatorFee
    // R8: Coll[Byte]   BankSingletonTokenId

    // ===== Relevant Transactions ===== //
    // 1. Mint Tx
    // Inputs: Bank, Proxy
    // DataInputs: None
    // Outputs: Bank, UserPK, MinerFee, TxOperatorFee
    // Context Variables: None
    // 2. Burn Tx
    // Inputs: Bank, Proxy
    // DataInputs: None
    // Outputs: Bank, UserPK, PhoenixFee, MinerFee, TxOperatorFee
    // Context Variables: None

    // ===== Compile Time Constants ($) ===== //
    // None

    // ===== Context Variables (@) ===== //
    // None

    // ===== Relevant Variables ===== //


}