package com.romix.feature.feat324

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat324Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat324UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat324FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat324UserSummary
)

data class Feat324UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat324NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat324Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat324Config = Feat324Config()
) {

    fun loadSnapshot(userId: Long): Feat324NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat324NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat324UserSummary {
        return Feat324UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat324FeedItem> {
        val result = java.util.ArrayList<Feat324FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat324FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat324UiMapper {

    fun mapToUi(model: List<Feat324FeedItem>): Feat324UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat324UiModel(
            header = UiText("Feat324 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat324UiModel =
        Feat324UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat324UiModel =
        Feat324UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat324UiModel =
        Feat324UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat324Service(
    private val repository: Feat324Repository,
    private val uiMapper: Feat324UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat324UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat324UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat324UserItem1(val user: CoreUser, val label: String)
data class Feat324UserItem2(val user: CoreUser, val label: String)
data class Feat324UserItem3(val user: CoreUser, val label: String)
data class Feat324UserItem4(val user: CoreUser, val label: String)
data class Feat324UserItem5(val user: CoreUser, val label: String)
data class Feat324UserItem6(val user: CoreUser, val label: String)
data class Feat324UserItem7(val user: CoreUser, val label: String)
data class Feat324UserItem8(val user: CoreUser, val label: String)
data class Feat324UserItem9(val user: CoreUser, val label: String)
data class Feat324UserItem10(val user: CoreUser, val label: String)

data class Feat324StateBlock1(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock2(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock3(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock4(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock5(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock6(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock7(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock8(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock9(val state: Feat324UiModel, val checksum: Int)
data class Feat324StateBlock10(val state: Feat324UiModel, val checksum: Int)

fun buildFeat324UserItem(user: CoreUser, index: Int): Feat324UserItem1 {
    return Feat324UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat324StateBlock(model: Feat324UiModel): Feat324StateBlock1 {
    return Feat324StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat324UserSummary> {
    val list = java.util.ArrayList<Feat324UserSummary>(users.size)
    for (user in users) {
        list += Feat324UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat324UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat324UiModel {
    val summaries = (0 until count).map {
        Feat324UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat324UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat324UiModel> {
    val models = java.util.ArrayList<Feat324UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat324AnalyticsEvent1(val name: String, val value: String)
data class Feat324AnalyticsEvent2(val name: String, val value: String)
data class Feat324AnalyticsEvent3(val name: String, val value: String)
data class Feat324AnalyticsEvent4(val name: String, val value: String)
data class Feat324AnalyticsEvent5(val name: String, val value: String)
data class Feat324AnalyticsEvent6(val name: String, val value: String)
data class Feat324AnalyticsEvent7(val name: String, val value: String)
data class Feat324AnalyticsEvent8(val name: String, val value: String)
data class Feat324AnalyticsEvent9(val name: String, val value: String)
data class Feat324AnalyticsEvent10(val name: String, val value: String)

fun logFeat324Event1(event: Feat324AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat324Event2(event: Feat324AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat324Event3(event: Feat324AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat324Event4(event: Feat324AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat324Event5(event: Feat324AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat324Event6(event: Feat324AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat324Event7(event: Feat324AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat324Event8(event: Feat324AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat324Event9(event: Feat324AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat324Event10(event: Feat324AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat324Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat324Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat324(u: CoreUser): Feat324Projection1 =
    Feat324Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat324Projection1> {
    val list = java.util.ArrayList<Feat324Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat324(u)
    }
    return list
}
