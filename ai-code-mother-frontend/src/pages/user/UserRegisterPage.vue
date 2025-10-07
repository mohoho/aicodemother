<template>
  <div id="userRegisterPage">
    <h2 class="title">用户注册</h2>
    <div class="desc">不写一行代码生成应用</div>
    <a-form
      :model="formState"
      name="basic"
      autocomplete="off"
      @finish="handleSubmit"
      @finishFailed="handleSubmitFailed"
    >
      <a-form-item
        label="账号"
        name="userAccount"
        :rules="[{ required: true, message: '请输入账号' }]"
      >
        <a-input v-model:value="formState.userAccount" placeHolder="请输入账号" />
      </a-form-item>

      <a-form-item
        label="密码"
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码!' },
          { min: 8, message: '密码长度不能少于8位' }
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeHolder="请输入密码" />
      </a-form-item>

      <a-form-item
        label="确认密码"
        name="checkPassword"
        :rules="[
          { required: true, message: '请确认密码!' },
          { validator: validateCheckPassword }
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeHolder="请再次输入密码" />
      </a-form-item>

      <div class="tips">
        已有账号 <RouterLink to="/user/login">去登录</RouterLink>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { register } from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
});

const router = useRouter();

// 自定义验证器：确认密码
const validateCheckPassword = (_rule: any, value: string) => {
  if (value && value !== formState.userPassword) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
};

const handleSubmit = async (values: any) => {
  const res = await register(values);
  // 注册成功
  if (res.data.code === 0) {
    message.success("注册成功，请登录");
    router.push({
      path: "/user/login",
      replace: true,
    })
  } else {
    message.error('注册失败-' + res.data.message)
  }

  console.log('Success:', values);
};

const handleSubmitFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo);
};
</script>

<style scoped>
#userRegisterPage {
  background: white;
  max-width: 720px;
  padding: 24px;
  margin: 24px auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}
</style>
