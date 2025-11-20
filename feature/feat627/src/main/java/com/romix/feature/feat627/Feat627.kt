package com.romix.feature.feat627

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat627Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat627UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat627FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat627UserSummary
)

data class Feat627UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat627NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat627Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat627Config = Feat627Config()
) {

    fun loadSnapshot(userId: Long): Feat627NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat627NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat627UserSummary {
        return Feat627UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat627FeedItem> {
        val result = java.util.ArrayList<Feat627FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat627FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat627UiMapper {

    fun mapToUi(model: List<Feat627FeedItem>): Feat627UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat627UiModel(
            header = UiText("Feat627 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat627UiModel =
        Feat627UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat627UiModel =
        Feat627UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat627UiModel =
        Feat627UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat627Service(
    private val repository: Feat627Repository,
    private val uiMapper: Feat627UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat627UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat627UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat627UserItem1(val user: CoreUser, val label: String)
data class Feat627UserItem2(val user: CoreUser, val label: String)
data class Feat627UserItem3(val user: CoreUser, val label: String)
data class Feat627UserItem4(val user: CoreUser, val label: String)
data class Feat627UserItem5(val user: CoreUser, val label: String)
data class Feat627UserItem6(val user: CoreUser, val label: String)
data class Feat627UserItem7(val user: CoreUser, val label: String)
data class Feat627UserItem8(val user: CoreUser, val label: String)
data class Feat627UserItem9(val user: CoreUser, val label: String)
data class Feat627UserItem10(val user: CoreUser, val label: String)

data class Feat627StateBlock1(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock2(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock3(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock4(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock5(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock6(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock7(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock8(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock9(val state: Feat627UiModel, val checksum: Int)
data class Feat627StateBlock10(val state: Feat627UiModel, val checksum: Int)

fun buildFeat627UserItem(user: CoreUser, index: Int): Feat627UserItem1 {
    return Feat627UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat627StateBlock(model: Feat627UiModel): Feat627StateBlock1 {
    return Feat627StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat627UserSummary> {
    val list = java.util.ArrayList<Feat627UserSummary>(users.size)
    for (user in users) {
        list += Feat627UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat627UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat627UiModel {
    val summaries = (0 until count).map {
        Feat627UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat627UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat627UiModel> {
    val models = java.util.ArrayList<Feat627UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat627AnalyticsEvent1(val name: String, val value: String)
data class Feat627AnalyticsEvent2(val name: String, val value: String)
data class Feat627AnalyticsEvent3(val name: String, val value: String)
data class Feat627AnalyticsEvent4(val name: String, val value: String)
data class Feat627AnalyticsEvent5(val name: String, val value: String)
data class Feat627AnalyticsEvent6(val name: String, val value: String)
data class Feat627AnalyticsEvent7(val name: String, val value: String)
data class Feat627AnalyticsEvent8(val name: String, val value: String)
data class Feat627AnalyticsEvent9(val name: String, val value: String)
data class Feat627AnalyticsEvent10(val name: String, val value: String)

fun logFeat627Event1(event: Feat627AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat627Event2(event: Feat627AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat627Event3(event: Feat627AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat627Event4(event: Feat627AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat627Event5(event: Feat627AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat627Event6(event: Feat627AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat627Event7(event: Feat627AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat627Event8(event: Feat627AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat627Event9(event: Feat627AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat627Event10(event: Feat627AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat627Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat627Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat627(u: CoreUser): Feat627Projection1 =
    Feat627Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat627Projection1> {
    val list = java.util.ArrayList<Feat627Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat627(u)
    }
    return list
}
