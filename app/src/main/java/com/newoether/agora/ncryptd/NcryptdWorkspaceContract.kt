package com.newoether.agora.ncryptd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire contract for the nCryptd workspace service.
 *
 * This is deliberately a transport contract, not a second local source of truth.
 * Implementations must obtain a short-lived user access token from the authenticated
 * session provider and must never embed a service-role key in the Android client.
 */
interface NcryptdWorkspaceApi {
    suspend fun getCurrentUser(): NcryptdUser
    suspend fun listWorkspaces(cursor: String? = null): NcryptdPage<NcryptdWorkspace>
    suspend fun listProjects(workspaceId: String, cursor: String? = null): NcryptdPage<NcryptdProject>
    suspend fun listTasks(projectId: String, cursor: String? = null): NcryptdPage<NcryptdTask>
    suspend fun getTask(taskId: String): NcryptdTask
}

@Serializable
data class NcryptdPage<T>(
    val items: List<T>,
    val nextCursor: String? = null,
)

@Serializable
data class NcryptdUser(
    val id: String,
    val email: String? = null,
    val displayName: String? = null,
)

@Serializable
data class NcryptdWorkspace(
    val id: String,
    val name: String,
    val role: String,
)

@Serializable
data class NcryptdProject(
    val id: String,
    val workspaceId: String,
    val name: String,
    val description: String? = null,
    val status: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class NcryptdTask(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String? = null,
    val status: String,
    val priority: String? = null,
    val assigneeId: String? = null,
    val dueAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class NcryptdApiError(
    val code: String,
    val message: String,
    val requestId: String? = null,
)

/**
 * Stable route names for the v1 mobile API. The backend must implement these
 * routes before the Android integration can be enabled in production.
 */
object NcryptdApiRoutes {
    const val VERSIONED_ROOT = "/api/mobile/v1"
    const val ME = "$VERSIONED_ROOT/me"
    const val WORKSPACES = "$VERSIONED_ROOT/workspaces"

    fun projects(workspaceId: String) =
        "$WORKSPACES/$workspaceId/projects"

    fun tasks(projectId: String) =
        "$VERSIONED_ROOT/projects/$projectId/tasks"

    fun task(taskId: String) =
        "$VERSIONED_ROOT/tasks/$taskId"
}
