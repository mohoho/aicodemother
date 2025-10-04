import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getLoginUser } from '@/api/userController.ts'

/**
 * 登录用户信息
 */

export const useLoginUserStore = defineStore('loginUser', () => {
  const count = ref(0)

  const loginUser = ref<API.LoginUserVO>({
    userName: '未登录',
  })

  // 获取用户登录信息
  async function fetchLoginUser() {
    const res = await getLoginUser()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  // 更新用户登录信息
  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
