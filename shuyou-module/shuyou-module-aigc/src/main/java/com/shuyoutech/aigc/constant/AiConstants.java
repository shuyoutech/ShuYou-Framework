package com.shuyoutech.aigc.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YangChao
 * @date 2025-05-15 09:49
 **/
public interface AiConstants {

    String MODEL = "model";
    String STREAM = "stream";
    String MESSAGES = "messages";
    String ROLE = "role";
    String CONTENT = "content";
    String TOOL_CALL_ID = "tool_call_id";
    String ROLE_SYSTEM = "system";
    String ROLE_ASSISTANT = "assistant";
    String ROLE_USER = "user";
    String ROLE_TOOL = "tool";
    String EVENT_STREAM_DATA = "data: ";
    String EVENT_ANSWER = "answer";
    String EVENT_END = "end";
    String EVENT_ERROR = "error";

    String MODEL_ALIYUN_QWEN_MAX = "qwen-max";
    String MODEL_ALIYUN_QWEN_PLUS = "qwen-plus";
    String MODEL_ALIYUN_QWEN_FLASH = "qwen-flash";
    String MODEL_ALIYUN_QWEN_TURBO = "qwen-turbo";
    String MODEL_ALIYUN_QWQ_PLUS = "qwq-plus";
    String MODEL_ALIYUN_QWEN_LONG = "qwen-long";
    String MODEL_ALIYUN_QWEN_OMNI_TURBO = "qwen-omni-turbo";
    String MODEL_ALIYUN_QWEN_OMNI_TURBO_REALTIME = "qwen-omni-turbo-realtime";
    String MODEL_ALIYUN_QVQ_MAX = "qvq-max";
    String MODEL_ALIYUN_QWEN_VL_MAX = "qwen-vl-max";
    String MODEL_ALIYUN_QWEN_VL_PLUS = "qwen-vl-plus";
    String MODEL_ALIYUN_QWEN_VL_OCR = "qwen-vl-ocr";
    String MODEL_ALIYUN_QWEN_AUDIO_TURBO = "qwen-audio-turbo";
    String MODEL_ALIYUN_QWEN_AUDIO_ASR = "qwen-audio-asr";
    String MODEL_ALIYUN_QWEN_MATH_PLUS = "qwen-math-plus";
    String MODEL_ALIYUN_QWEN_MATH_TURBO = "qwen-math-turbo";
    String MODEL_ALIYUN_QWEN_CODER_PLUS = "qwen-coder-plus";
    String MODEL_ALIYUN_QWEN_CODER_FLASH = "qwen3-coder-flash";
    String MODEL_ALIYUN_QWEN_MT_PLUS = "qwen-mt-plus";
    String MODEL_ALIYUN_QWEN_MT_TURBO = "qwen-mt-turbo";
    String MODEL_ALIYUN_QWEN_DOC_TURBO = "qwen-doc-turbo";
    String MODEL_ALIYUN_TEXT_EMBEDDING_V4 = "text-embedding-v4";
    String MODEL_ALIYUN_MULTIMODAL_EMBEDDING_V1 = "multimodal-embedding-v1";
    String MODEL_ALIYUN_WAN2_2_T2I_PLUS = "wan2.2-t2i-plus";
    String MODEL_ALIYUN_WAN2_2_T2I_FLASH = "wan2.2-t2i-flash";
    String MODEL_ALIYUN_WANX2_1_IMAGEEDIT = "wanx2.1-imageedit";
    String MODEL_ALIYUN_WANX_BACKGROUND_GENERATION = "wanx-background-generation-v2";
    String MODEL_ALIYUN_WORDART_SEMANTIC = "wordart-semantic";
    String MODEL_ALIYUN_WORDART_TEXTURE = "wordart-texture";
    String MODEL_ALIYUN_WAN2_2_T2V_PLUS = "wan2.2-t2v-plus";
    String MODEL_ALIYUN_WAN2_2_I2V_PLUS = "wan2.2-i2v-plus";
    String MODEL_ALIYUN_wanx2_1_KF2V_PLUS = "wanx2.1-kf2v-plus";
    String MODEL_DEEPSEEK_CHAT = "deepseek-chat";
    String MODEL_DEEPSEEK_REASONER = "deepseek-reasoner";
    String MODEL_OPENAI_GPT_5 = "gpt-5";
    String MODEL_OPENAI_GPT_5_MINI = "gpt-5-mini";
    String MODEL_OPENAI_GPT_5_NANO = "gpt-5-nano";
    String MODEL_OPENAI_GPT_5_CHAT_LATEST = "gpt-5-chat-latest";
    String MODEL_OPENAI_GPT_4_1 = "gpt-4.1";
    String MODEL_OPENAI_GPT_4_1_MINI = "gpt-4.1-mini";
    String MODEL_OPENAI_GPT_4_1_NANO = "gpt-4.1-nano";
    String MODEL_OPENAI_O4_MINI = "o4-mini";
    String MODEL_OPENAI_O3 = "o3";
    String MODEL_OPENAI_O3_MINI = "o3-mini";
    String MODEL_OPENAI_O1 = "o1";
    String MODEL_OPENAI_O1_PREVIEW = "o1-preview";
    String MODEL_OPENAI_O1_MINI = "o1-mini";
    String MODEL_OPENAI_GPT_4O = "gpt-4o";
    String MODEL_OPENAI_GPT_4O_AUDIO_PREVIEW = "gpt-4o-audio-preview";
    String MODEL_OPENAI_GPT_4O_MINI_AUDIO_PREVIEW = "gpt-4o-mini-audio-preview";
    String MODEL_OPENAI_GPT_4O_SEARCH_PREVIEW = "gpt-4o-search-preview";
    String MODEL_OPENAI_GPT_4O_MINI_SEARCH_PREVIEW = "gpt-4o-mini-search-preview";
    String MODEL_OPENAI_CHATGPT_4O_LATEST = "chatgpt-4o-latest";
    String MODEL_OPENAI_CODEX_MINI_LATEST = "codex-mini-latest";
    String MODEL_OPENAI_GPT_4O_MINI = "gpt-4o-mini";
    String MODEL_OPENAI_GPT_4_TURBO = "gpt-4-turbo";
    String MODEL_OPENAI_GPT_4 = "gpt-4";
    String MODEL_OPENAI_GPT_3_5_TURBO = "gpt-3.5-turbo";
    String MODEL_OPENAI_GPT_IMAGE_1 = "gpt-image-1";
    String MODEL_OPENAI_DALL_E_2 = "dall-e-2";
    String MODEL_OPENAI_DALL_E_3 = "dall-e-3";
    String MODEL_OPENAI_WHISPER_1 = "whisper-1";
    String MODEL_OPENAI_GPT_4O_TRANSCRIBE = "gpt-4o-transcribe";
    String MODEL_OPENAI_GPT_4O_MINI_TRANSCRIBE = "gpt-4o-mini-transcribe";
    String MODEL_OPENAI_TEXT_EMBEDDING_ADA_002 = "text-embedding-ada-002";
    String MODEL_OPENAI_TEXT_EMBEDDING_3_SMALL = "text-embedding-3-small";
    String MODEL_OPENAI_TEXT_EMBEDDING_3_LARGE = "text-embedding-3-large";
    String MODEL_OPENAI_OMNI_MODERATION_LATEST = "omni-moderation-latest";
    String MODEL_GOOGLE_GEMINI_2_5_PRO = "gemini-2.5-pro";
    String MODEL_GOOGLE_GEMINI_2_5_FLASH = "gemini-2.5-flash";
    String MODEL_GOOGLE_GEMINI_2_5_FLASH_LITE = "gemini-2.5-flash-lite";
    String MODEL_ANTHROPIC_CLAUDE_OPUS_4_1 = "claude-opus-4-1-20250805";
    String MODEL_ANTHROPIC_CLAUDE_OPUS_4 = "claude-opus-4-20250514";
    String MODEL_ANTHROPIC_CLAUDE_SONNET_4 = "claude-sonnet-4-20250514";
    String MODEL_XAI_GROK_4 = "grok-4-0709";
    String MODEL_XAI_GROK_3 = "grok-3";
    String MODEL_XAI_GROK_3_MINI = "grok-3-mini";
    String MODEL_XAI_GROK_2_IMAGE = "grok-2-image-1212";
    String MODEL_RUNWAY_GEN4_IMAGE = "gen4_image";
    String MODEL_RUNWAY_GEN4_IMAGE_TURBO = "gen4_image_turbo";
    String MODEL_RUNWAY_GEN4_TURBO = "gen4_turbo";
    String MODEL_RUNWAY_GEN4_ALEPH = "gen4_aleph";
    String MODEL_RUNWAY_GEN3A_TURBO = "gen3a_turbo";
    String MODEL_RUNWAY_UPSCALE_V1 = "upscale_v1";
    String MODEL_RUNWAY_ACT_TWO = "act_two";
    String MODEL_KLING_V2_1_MASTER = "kling-v2-1-master";
    String MODEL_KLING_V2_1 = "kling-v2-1";
    String MODEL_KLING_V2 = "kling-v2";

    List<String> CHAT_MODEL_DEEPSEEK_LIST = new ArrayList<>() {
        {
            add(MODEL_DEEPSEEK_CHAT);
            add(MODEL_DEEPSEEK_REASONER);
        }
    };

    List<String> CHAT_MODEL_ALIYUN_LIST = new ArrayList<>() {
        {
            add(MODEL_ALIYUN_QWEN_MAX);
            add(MODEL_ALIYUN_QWEN_PLUS);
            add(MODEL_ALIYUN_QWEN_FLASH);
            add(MODEL_ALIYUN_QWEN_TURBO);
            add(MODEL_ALIYUN_QWQ_PLUS);
            add(MODEL_ALIYUN_QWEN_LONG);
            add(MODEL_ALIYUN_QWEN_OMNI_TURBO);
            add(MODEL_ALIYUN_QWEN_OMNI_TURBO_REALTIME);
            add(MODEL_ALIYUN_QVQ_MAX);
            add(MODEL_ALIYUN_QWEN_VL_MAX);
            add(MODEL_ALIYUN_QWEN_VL_PLUS);
            add(MODEL_ALIYUN_QWEN_VL_OCR);
            add(MODEL_ALIYUN_QWEN_AUDIO_TURBO);
            add(MODEL_ALIYUN_QWEN_AUDIO_ASR);
            add(MODEL_ALIYUN_QWEN_MATH_PLUS);
            add(MODEL_ALIYUN_QWEN_MATH_TURBO);
            add(MODEL_ALIYUN_QWEN_CODER_PLUS);
            add(MODEL_ALIYUN_QWEN_CODER_FLASH);
            add(MODEL_ALIYUN_QWEN_MT_PLUS);
            add(MODEL_ALIYUN_QWEN_MT_TURBO);
            add(MODEL_ALIYUN_QWEN_DOC_TURBO);
        }
    };

    List<String> CHAT_MODEL_OPENAI_LIST = new ArrayList<>() {
        {
            add(MODEL_OPENAI_GPT_5);
            add(MODEL_OPENAI_GPT_5_MINI);
            add(MODEL_OPENAI_GPT_5_NANO);
            add(MODEL_OPENAI_GPT_5_CHAT_LATEST);
            add(MODEL_OPENAI_GPT_4_1);
            add(MODEL_OPENAI_GPT_4_1_MINI);
            add(MODEL_OPENAI_GPT_4_1_NANO);
            add(MODEL_OPENAI_O4_MINI);
            add(MODEL_OPENAI_O3);
            add(MODEL_OPENAI_O3_MINI);
            add(MODEL_OPENAI_O1);
            add(MODEL_OPENAI_O1_PREVIEW);
            add(MODEL_OPENAI_O1_MINI);
            add(MODEL_OPENAI_GPT_4O);
            add(MODEL_OPENAI_GPT_4O_AUDIO_PREVIEW);
            add(MODEL_OPENAI_GPT_4O_MINI_AUDIO_PREVIEW);
            add(MODEL_OPENAI_GPT_4O_SEARCH_PREVIEW);
            add(MODEL_OPENAI_GPT_4O_MINI_SEARCH_PREVIEW);
            add(MODEL_OPENAI_CHATGPT_4O_LATEST);
            add(MODEL_OPENAI_CODEX_MINI_LATEST);
            add(MODEL_OPENAI_GPT_4O_MINI);
            add(MODEL_OPENAI_GPT_4_TURBO);
            add(MODEL_OPENAI_GPT_4);
            add(MODEL_OPENAI_GPT_3_5_TURBO);
        }
    };

    List<String> IMAGE_MODEL_OPENAI_LIST = new ArrayList<>() {
        {
            add(MODEL_OPENAI_GPT_IMAGE_1);
            add(MODEL_OPENAI_DALL_E_2);
            add(MODEL_OPENAI_DALL_E_3);
        }
    };

    List<String> AUDIO_MODEL_OPENAI_LIST = new ArrayList<>() {
        {
            add(MODEL_OPENAI_WHISPER_1);
            add(MODEL_OPENAI_GPT_4O_TRANSCRIBE);
            add(MODEL_OPENAI_GPT_4O_MINI_TRANSCRIBE);
        }
    };

    List<String> EMBEDDING_MODEL_OPENAI_LIST = new ArrayList<>() {
        {
            add(MODEL_OPENAI_TEXT_EMBEDDING_ADA_002);
            add(MODEL_OPENAI_TEXT_EMBEDDING_3_SMALL);
            add(MODEL_OPENAI_TEXT_EMBEDDING_3_LARGE);
        }
    };

    List<String> MODERATION_MODEL_OPENAI_LIST = new ArrayList<>() {
        {
            add(MODEL_OPENAI_OMNI_MODERATION_LATEST);
        }
    };


    String IMAGES_EDITS = "/images/edits";
    String API_URL_ALIYUN = "https://dashscope.aliyuncs.com";
    String API_KEY_ALIYUN = "sk-c8e34c0557a945819a6a05061da754f6";
    String ALIYUN_HEADER_DASH_SCOPE_ASYNC = "X-DashScope-Async";
    String ALIYUN_HEADER_DASH_SCOPE_ASYNC_VALUE = "enable";
    String ALIYUN_CHAT_COMPLETIONS = "/compatible-mode/v1/chat/completions";
    String ALIYUN_EMBEDDINGS = "/compatible-mode/v1/embeddings";
    String ALIYUN_IMAGES_SYNTHESIS = "/api/v1/services/aigc/text2image/image-synthesis";
    String ALIYUN_TASKS = "/api/v1/tasks/";
    String ALIYUN_IMAGES_IMAGE_SYNTHESIS = "/api/v1/services/aigc/image2image/image-synthesis";
    String ALIYUN_IMAGES_GENERATION = "/api/v1/services/aigc/image-generation/generation";
    String ALIYUN_IMAGES_OUT_PAINTING = "/api/v1/services/aigc/image2image/out-painting";
    String ALIYUN_IMAGES_VIRTUAL_MODEL = "/api/v1/services/aigc/virtualmodel/generation";
    String ALIYUN_BACKGROUND_GENERATION = "/api/v1/services/aigc/background-generation/generation";
    String ALIYUN_ALBUM_GEN_POTRAIT = "/api/v1/services/aigc/album/gen_potrait";
    String ALIYUN_WORDART_SEMANTIC = "/api/v1/services/aigc/wordart/semantic";
    String ALIYUN_WORDART_TEXTURE = "/api/v1/services/aigc/wordart/texture";
    String ALIYUN_VIDEO_SYNTHESIS = "/api/v1/services/aigc/video-generation/video-synthesis";
    String ALIYUN_IMAGE2_VIDEO_SYNTHESIS = "/api/v1/services/aigc/image2video/video-synthesis";
    String ALIYUN_IMAGE2_VIDEO_AA_DETECT = "/api/v1/services/aigc/image2video/aa-detect";
    String ALIYUN_IMAGE2_VIDEO_AA_TEMPLATE = "/api/v1/services/aigc/image2video/aa-template-generation";
    String ALIYUN_IMAGE2_VIDEO_FACE_DETECT = "/api/v1/services/aigc/image2video/face-detect";
    String ALIYUN_AUDIO_GENERATION = "/api/v1/services/aigc/multimodal-generation/generation";
    String ALIYUN_AUDIO_ASR_TRANSCRIPTION = "/api/v1/services/audio/asr/transcription";


    String API_URL_DEEPSEEK = "https://api.deepseek.com";
    String API_KEY_DEEPSEEK = "";
    String DEEPSEEK_CHAT_COMPLETIONS = "/chat/completions";
    String DEEPSEEK_BETA_COMPLETIONS = "/beta/completions";

    String API_URL_OPENAI = "https://api.openai.com";
    String API_KEY_OPENAI = "";
    String OPENAI_CHAT_COMPLETIONS = "/v1/chat/completions";
    String OPENAI_IMAGES_GENERATIONS = "/v1/images/generations";
    String OPENAI_IMAGES_VARIATIONS = "/v1/images/variations";
    String OPENAI_AUDIO_SPEECH = "/v1/audio/speech";
    String OPENAI_AUDIO_TRANSCRIPTIONS = "/v1/audio/transcriptions";
    String OPENAI_AUDIO_TRANSLATIONS = "/v1/audio/translations";
    String OPENAI_EMBEDDINGS = "/v1/embeddings";
    String OPENAI_MODERATIONS = "/v1/moderations";

    String API_URL_GOOGLE = "https://generativelanguage.googleapis.com";
    String API_KEY_GOOGLE = "";
    String GOOGLE_GENERATE_CONTENT = "/v1beta/models/{}:generateContent?key={}";

    String API_URL_ANTHROPIC = "https://api.anthropic.com";
    String API_KEY_ANTHROPIC = "";
    String ANTHROPIC_CHAT_COMPLETIONS = "/v1/messages";

    String API_URL_XAI = "https://api.x.ai";
    String API_KEY_XAI = "";

    String API_URL_RUNWAY = "https://api.dev.runwayml.com";
    String API_KEY_RUNWAY = "";
    String RUNWAY_HEADER_X_RUNWAY_VERSION = "X-Runway-Version";
    String RUNWAY_HEADER_X_RUNWAY_VERSION_VALUE = "2024-11-06";
    String RUNWAY_TEXT_TO_IMAGE = "/v1/text_to_image";
    String RUNWAY_IMAGE_TO_VIDEO = "/v1/image_to_video";
    String RUNWAY_VIDEO_UPSCALE = "/v1/video_upscale";
    String RUNWAY_CHARACTER_PERFORMANCE = "/v1/character_performance";
    String RUNWAY_TASKS = "/v1/tasks/";

    String API_URL_STABLE_DIFFUSION = "https://api.stability.ai";
    String API_KEY_STABLE_DIFFUSION = "";

    String API_URL_KLING = "https://api.klingai.com";
    String API_KEY_KLING = "";
    String KLING_IMAGES_GENERATIONS = "/v1/images/generations";
    String KLING_IMAGES_VIRTUAL_TRY_ON = "/v1/images/kolors-virtual-try-on";
    String KLING_VIDEOS_TEXT2VIDEO = "/v1/videos/text2video";
    String KLING_VIDEOS_IMAGE2VIDEO = "/v1/videos/image2video";
    String KLING_VIDEOS_MULTI_IMAGE2VIDEO = "/v1/videos/multi-image2video";
    String KLING_VIDEOS_VIDEO_EXTEND = "/v1/videos/video-extend";
    String KLING_VIDEOS_VIDEO_LIP_SYNC = "/v1/videos/lip-sync";
    String KLING_VIDEOS_VIDEO_EFFECTS = "/v1/videos/effects";

    String API_URL_OPEN_ROUTER = "https://openrouter.ai/api";
    String API_KEY_OPEN_ROUTER = "";

    String USER_CHAT_SERVICE = "userChatServiceImpl";
    String USER_IMAGE_SERVICE = "userImageServiceImpl";
    String USER_VIDEO_SERVICE = "userVideoServiceImpl";
    String APP_CHAT_SERVICE = "appChatServiceImpl";

    String KNOWLEDGE_ID = "knowledgeId";
    String DOC_ID = "docId";
    String PROVIDER = "provider";
    String KNOWLEDGE = "knowledge";
    String VECTOR_STORE = "vectorStore";
    String APP = "app";

}
