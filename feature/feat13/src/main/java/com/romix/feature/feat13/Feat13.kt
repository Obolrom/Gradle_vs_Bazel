package com.romix.feature.feat13

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat13Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat13UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat13FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat13UserSummary
)

data class Feat13UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat13NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat13Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat13Config = Feat13Config()
) {

    fun loadSnapshot(userId: Long): Feat13NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat13NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat13UserSummary {
        return Feat13UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat13FeedItem> {
        val result = java.util.ArrayList<Feat13FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat13FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat13UiMapper {

    fun mapToUi(model: List<Feat13FeedItem>): Feat13UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat13UiModel(
            header = UiText("Feat13 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat13UiModel =
        Feat13UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat13UiModel =
        Feat13UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat13UiModel =
        Feat13UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat13Service(
    private val repository: Feat13Repository,
    private val uiMapper: Feat13UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat13UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat13UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat13UserItem1(val user: CoreUser, val label: String)
data class Feat13UserItem2(val user: CoreUser, val label: String)
data class Feat13UserItem3(val user: CoreUser, val label: String)
data class Feat13UserItem4(val user: CoreUser, val label: String)
data class Feat13UserItem5(val user: CoreUser, val label: String)
data class Feat13UserItem6(val user: CoreUser, val label: String)
data class Feat13UserItem7(val user: CoreUser, val label: String)
data class Feat13UserItem8(val user: CoreUser, val label: String)
data class Feat13UserItem9(val user: CoreUser, val label: String)
data class Feat13UserItem10(val user: CoreUser, val label: String)

data class Feat13StateBlock1(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock2(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock3(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock4(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock5(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock6(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock7(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock8(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock9(val state: Feat13UiModel, val checksum: Int)
data class Feat13StateBlock10(val state: Feat13UiModel, val checksum: Int)

fun buildFeat13UserItem(user: CoreUser, index: Int): Feat13UserItem1 {
    return Feat13UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat13StateBlock(model: Feat13UiModel): Feat13StateBlock1 {
    return Feat13StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat13UserSummary> {
    val list = java.util.ArrayList<Feat13UserSummary>(users.size)
    for (user in users) {
        list += Feat13UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat13UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat13UiModel {
    val summaries = (0 until count).map {
        Feat13UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat13UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat13UiModel> {
    val models = java.util.ArrayList<Feat13UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat13AnalyticsEvent1(val name: String, val value: String)
data class Feat13AnalyticsEvent2(val name: String, val value: String)
data class Feat13AnalyticsEvent3(val name: String, val value: String)
data class Feat13AnalyticsEvent4(val name: String, val value: String)
data class Feat13AnalyticsEvent5(val name: String, val value: String)
data class Feat13AnalyticsEvent6(val name: String, val value: String)
data class Feat13AnalyticsEvent7(val name: String, val value: String)
data class Feat13AnalyticsEvent8(val name: String, val value: String)
data class Feat13AnalyticsEvent9(val name: String, val value: String)
data class Feat13AnalyticsEvent10(val name: String, val value: String)

fun logFeat13Event1(event: Feat13AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat13Event2(event: Feat13AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat13Event3(event: Feat13AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat13Event4(event: Feat13AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat13Event5(event: Feat13AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat13Event6(event: Feat13AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat13Event7(event: Feat13AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat13Event8(event: Feat13AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat13Event9(event: Feat13AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat13Event10(event: Feat13AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat13Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat13Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat13(u: CoreUser): Feat13Projection1 =
    Feat13Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat13Projection1> {
    val list = java.util.ArrayList<Feat13Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat13(u)
    }
    return list
}
