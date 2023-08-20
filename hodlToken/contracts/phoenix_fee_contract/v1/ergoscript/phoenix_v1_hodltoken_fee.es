{

    // ===== Contract Information ===== //
    // Name: Phoenix HodlToken Fee
    // Description: Contract guarding the fee box for the HodlToken protocol.
    // Version: 1.0.0
    // Author: Luca D'Angelo (ldgaetano@protonmail.com), MGPai

    // ===== Box Contents ===== //
    // Tokens
    // 1. (HodlTokenId, HoldTokenFeeAmount)
    // None
    // Registers
    // None

    // ===== Relevant Transactions ===== //
    // 1. Fee Distribution Tx
    // Inputs: PhoenixFee1, ... , PhoenixFeeM
    // DataInputs: None
    // Outputs: Bruno, Pulsarz, Phoenix, Kushti, Kras, MinerFee
    // Context Variables: None

    // ===== Compile Time Constants ($) ===== //
    // $minerFee: Long
    // $brunoNum: Long
    // $phoenixNum: Long
    // $kushtiNum: Long

    // ===== Context Variables (@) ===== //
    // None

    // ===== Relevant Variables ===== //
    val minerFeeErgoTreeBytesHash: Coll[Byte] = fromBase16("e540cceffd3b8dd0f401193576cc413467039695969427df94454193dddfb375")

    val hodlTokenId: Coll[Byte] = SELF.tokens(0)._1
    val feeDenom: Long   = 100L

    val brunoAddress: SigmaProp   = PK("9exfustUCPDKXsfDrGNrmtkyLDwAie2rKKdUsPVa26RuBFaYeCL")
    val phoenixAddress: SigmaProp = PK("9iPs1ujGj2eKXVg82aGyAtUtQZQWxFaki48KFixoaNmUAoTY6wV")
    val kushtiAddress: SigmaProp  = PK("9iE2MadGSrn1ivHmRZJWRxzHffuAk6bPmEv6uJmPHuadBY8td5u")

    // ===== Fee Distribution Tx ===== //
    val validFeeDistributionTx: Boolean = {

        // Outputs
        val brunoBoxOUT: Box    = OUTPUTS(0)
        val pulsarzBoxOUT: Box  = OUTPUTS(1)
        val phoenixBoxOUT: Box  = OUTPUTS(2)
        val kushtiBoxOUT: Box   = OUTPUTS(3)
        val krasBoxOUT: Box     = OUTPUTS(4)
        val minerFeeBoxOUT: Box = OUTPUTS(5)

        val devAmount: Long = OUTPUTS.map({ (output: Box) => if (output.tokens(0)._1 == hodlTokenId) output.tokens(0)._2 else 0L }).fold(0L, { (acc: Long, curr: Long) => acc + curr })

        val validDevBoxes: Boolean = {

            val brunoAmount: Long   = ($brunoNum * devAmount) / feeDenom
            val phoenixAmount: Long = ($phoenixNum * devAmount) / feeDenom
            val kushtiAmount: Long  = ($kushtiNum * devAmount) / feeDenom

            val validBruno: Boolean   = (brunoBoxOUT.tokens.filter({ (t: (Coll[Byte], Long)) => t._1 == hodlTokenId })(0)._2 == brunoAmount) && (brunoBoxOUT.propositionBytes == brunoAddress.propBytes)
            val validPhoenix: Boolean = (phoenixBoxOUT.tokens.filter({ (t: (Coll[Byte], Long)) => t._1 == hodlTokenId })(0)._2 == phoenixAmount) && (phoenixBoxOUT.propositionBytes == phoenixAddress.propBytes)
            val validKushti: Boolean  = (kushtiBoxOUT.tokens.filter({ (t: (Coll[Byte], Long)) => t._1 == hodlTokenId })(0)._2 == kushtiAmount) && (kushtiBoxOUT.propositionBytes == kushtiAddress.propBytes)

            allOf(Coll(
                validBruno,
                validPhoenix,
                validKushti
            ))

        }

        val validMinerFee: Boolean = {

            allOf(Coll(
                (minerFeeBoxOUT.value >= $minerFee), // In case the miner fee increases in the future
                (blake2b256(minerFeeBoxOUT.propositionBytes) == minerFeeErgoTreeBytesHash)
            ))

        }

        val validOutputSize: Boolean = (OUTPUTS.size == 6)

        allOf(Coll(
            validDevBoxes,
            validMinerFee,
            validOutputSize
        ))

    }

    sigmaProp(validFeeDistributionTx) && atLeast(1, Coll(brunoAddress, phoenixAddress, kushtiAddress)) // Done so we are incentivized to not spam the miner fee.

}
