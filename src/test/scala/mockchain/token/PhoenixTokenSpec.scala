package mockchain.token

import mockClient.Common
import mockchain.PhoenixCommon
import org.ergoplatform.appkit.InputBox
import org.ergoplatform.sdk.ErgoToken
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PhoenixTokenSpec extends AnyFlatSpec
  with Matchers
  with Common
  with PhoenixCommon {

  val minAmount = 1000000L

  def hodlTokenPrice(hodlBoxIn: InputBox): Long = {
    // preserving terminology from the contract
    val reserveIn = hodlBoxIn.getTokens.get(2).getValue
    val totalTokenSupply =
      hodlBoxIn.getRegisters.get(0).getValue.asInstanceOf[Long] // R4
    val hodlCoinsIn: Long = hodlBoxIn.getTokens.get(1).getValue
    val hodlCoinsCircIn: Long = totalTokenSupply - hodlCoinsIn
    val precisionFactor = extractPrecisionFactor(hodlBoxIn)
    ((BigInt(reserveIn) * BigInt(precisionFactor)) / BigInt(
      hodlCoinsCircIn
    )).toLong
  }

  // amount of (nano) ERGs needed to mint given amount of hodlcoins against given hodl bank
  def mintAmountToken(hodlBoxIn: InputBox, hodlMintAmt: Long): Long = {
    val price = hodlTokenPrice(hodlBoxIn)
    val precisionFactor = extractPrecisionFactor(hodlBoxIn)
    hodlMintAmt * price / precisionFactor
  }

  /** @return amount of (nano) ERGs which can be released to when given amount of hodlcoins burnt to user,
    *         and also dev fee
    */
  def burnTokenAmount(hodlBoxIn: InputBox, hodlBurnAmt: Long): (Long, Long) = {
    val feeDenom = 1000L

    val bankFee =
      hodlBoxIn.getRegisters.get(4).getValue.asInstanceOf[Long] // R8
    val devFee = hodlBoxIn.getRegisters.get(3).getValue.asInstanceOf[Long] // R7

    val price = hodlTokenPrice(hodlBoxIn)
    val precisionFactor = extractPrecisionFactor(hodlBoxIn)
    val beforeFees = hodlBurnAmt * price / precisionFactor
    val bankFeeAmount: Long = (beforeFees * bankFee) / feeDenom
    val devFeeAmount: Long = (beforeFees * devFee) / feeDenom
    val expectedAmountWithdrawn: Long =
      beforeFees - bankFeeAmount - devFeeAmount
    (expectedAmountWithdrawn, devFeeAmount)
  }


  "PhoenixTokenMintOperation" should "work correctly when all conditions are satisfied" in {

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount)

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount),
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    noException shouldBe thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    }
  }

  "PhoenixTokenMintOperation" should "fail when more hodl taken" in {

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount)

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount - 1), // <<-- this line has changed
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    the[Exception] thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    } should have message "Script reduced to false"
  }

  "PhoenixTokenMintOperation" should "fail when less tokens paid" in {

    val tokenAmount = 10000000 * 1000000000L
    val hodlErgAmount = totalSupply / 10 * 9
    val hodlMintAmount = 20

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlErgAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val price = hodlTokenPrice(hodlBox)

    require(
      hodlBox.getTokens.get(2).getValue >= totalSupply - hodlErgAmount,
      "never-decreasing theorem does not hold"
    )
    require(
      price == 2000000,
      "Price does not correspond to manually calculated value"
    )

    val tokenMintAmount = mintAmountToken(hodlBox, hodlMintAmount)
    require(
      tokenMintAmount == 40,
      s"Token ($tokenMintAmount) delta does not correspond to manually calculated value "
    )

    val fundingBox = outBoxObj
      .genericContractBox(
        compiler.compileDummyContract(),
        fundingBoxValue,
        Seq(new ErgoToken(tokenId, tokenMintAmount))
      ).convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount + tokenMintAmount - 1) // <<-- this line has changed

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlErgAmount - hodlMintAmount),
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.hodlMintBox(
      userAddress,
      new ErgoToken(hodlTokenId, hodlMintAmount),
      minAmount
    )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox)
    )

    the[Exception] thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    } should have message "Script reduced to false"
  }

  "PhoenixTokenBurnOperation" should "succeed when all conditions are met" in {

    val tokenAmount = 10000000 * 1000000000L
    val hodlTokenAmount = 40000000 * 1000000000L

    val hodlBurnAmount = 2000

    val hodlSingleton = new ErgoToken(hodlBankNft, 1L)
    val hodlTokens = new ErgoToken(hodlTokenId, hodlTokenAmount)
    val tokensBefore = new ErgoToken(tokenId, tokenAmount)

    val hodlBox = outBoxObj
      .hodlBankBox(
        phoenixContractToken,
        hodlSingleton,
        hodlTokens,
        totalSupply,
        precisionFactor,
        minBankValue,
        bankFee,
        devFee,
        minAmount,
        Some(tokensBefore)
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val (userBoxAmount, devFeeAmount) = burnTokenAmount(hodlBox, hodlBurnAmount)
    println("uba: " + userBoxAmount)
    println("dfa: " + devFeeAmount)

    val fundingBox = outBoxObj
      .tokenOutBox(
        Array(new ErgoToken(hodlTokenId, hodlBurnAmount)),
        compiler.compileDummyContract().toAddress,
        fundingBoxValue
      )
      .convertToInputWith(fakeTxId1, fakeIndex)

    val tokensAfter = new ErgoToken(tokenId, tokenAmount - userBoxAmount - devFeeAmount)

    val hodlOutBox = outBoxObj.hodlBankBox(
      phoenixContractToken,
      hodlSingleton,
      new ErgoToken(hodlTokenId, hodlTokenAmount + hodlBurnAmount),
      totalSupply,
      precisionFactor,
      minBankValue,
      bankFee,
      devFee,
      minAmount,
      Some(tokensAfter)
    )

    val recipientBox = outBoxObj.tokenOutBox(
      Array(new ErgoToken(tokenId, userBoxAmount)),
      userAddress,
      minAmount
    )

    val devFeeBox =
      outBoxObj.tokenOutBox(
        Array(new ErgoToken(tokenId, devFeeAmount)),
        feeContractToken.toAddress,
        minAmount
      )

    val unsignedTransaction = txHelper.buildUnsignedTransaction(
      inputs = Array(hodlBox, fundingBox),
      outputs = Array(hodlOutBox, recipientBox, devFeeBox)
    )

    require(hodlOutBox.getTokens.get(2).getValue == hodlBox.getTokens.get(2).getValue - 1940)

    noException shouldBe thrownBy {
      txHelper.signTransaction(
        unsignedTransaction
      )
    }

  }

}