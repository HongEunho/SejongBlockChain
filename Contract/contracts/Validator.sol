pragma solidity  >=0.6.0 <=0.7.4;

contract Validator {//데이터 검증, 사용자가 서명한 올바른데이터인지 확인 
  function recoverAddress(bytes32 msgHash, uint8 v, bytes32 r, bytes32 s) public view returns(address) {
      bytes memory prefix = "\x19Ethereum Signed Message:\n32";
      bytes32 prefixedHash = keccak256(prefix);//keccak은 이더리움-SHA-3 해시를 계산해서 bytes32형태로 반환함
      return ecrecover(prefixedHash, v, r, s);//ecrecover 타원 곡선 서명으로부터 address와 연관된 공개키를 복구하며
      //오류시엔 0을 반환함 
  }
  function verify(address addr, bytes32 msgHash, uint8 v, bytes32 r, bytes32 s) public view returns(bool) {
      return addr == recoverAddress(msgHash, v, r, s);
  }
}