package com.romix.feature.feat458

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat458Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat458UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat458FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat458UserSummary
)

data class Feat458UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat458NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat458Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat458Config = Feat458Config()
) {

    fun loadSnapshot(userId: Long): Feat458NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat458NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat458UserSummary {
        return Feat458UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat458FeedItem> {
        val result = java.util.ArrayList<Feat458FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat458FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat458UiMapper {

    fun mapToUi(model: List<Feat458FeedItem>): Feat458UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat458UiModel(
            header = UiText("Feat458 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat458UiModel =
        Feat458UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat458UiModel =
        Feat458UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat458UiModel =
        Feat458UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat458Service(
    private val repository: Feat458Repository,
    private val uiMapper: Feat458UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat458UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat458UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat458UserItem1(val user: CoreUser, val label: String)
data class Feat458UserItem2(val user: CoreUser, val label: String)
data class Feat458UserItem3(val user: CoreUser, val label: String)
data class Feat458UserItem4(val user: CoreUser, val label: String)
data class Feat458UserItem5(val user: CoreUser, val label: String)
data class Feat458UserItem6(val user: CoreUser, val label: String)
data class Feat458UserItem7(val user: CoreUser, val label: String)
data class Feat458UserItem8(val user: CoreUser, val label: String)
data class Feat458UserItem9(val user: CoreUser, val label: String)
data class Feat458UserItem10(val user: CoreUser, val label: String)

data class Feat458StateBlock1(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock2(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock3(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock4(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock5(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock6(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock7(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock8(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock9(val state: Feat458UiModel, val checksum: Int)
data class Feat458StateBlock10(val state: Feat458UiModel, val checksum: Int)

fun buildFeat458UserItem(user: CoreUser, index: Int): Feat458UserItem1 {
    return Feat458UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat458StateBlock(model: Feat458UiModel): Feat458StateBlock1 {
    return Feat458StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat458UserSummary> {
    val list = java.util.ArrayList<Feat458UserSummary>(users.size)
    for (user in users) {
        list += Feat458UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat458UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat458UiModel {
    val summaries = (0 until count).map {
        Feat458UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat458UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat458UiModel> {
    val models = java.util.ArrayList<Feat458UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat458AnalyticsEvent1(val name: String, val value: String)
data class Feat458AnalyticsEvent2(val name: String, val value: String)
data class Feat458AnalyticsEvent3(val name: String, val value: String)
data class Feat458AnalyticsEvent4(val name: String, val value: String)
data class Feat458AnalyticsEvent5(val name: String, val value: String)
data class Feat458AnalyticsEvent6(val name: String, val value: String)
data class Feat458AnalyticsEvent7(val name: String, val value: String)
data class Feat458AnalyticsEvent8(val name: String, val value: String)
data class Feat458AnalyticsEvent9(val name: String, val value: String)
data class Feat458AnalyticsEvent10(val name: String, val value: String)

fun logFeat458Event1(event: Feat458AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat458Event2(event: Feat458AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat458Event3(event: Feat458AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat458Event4(event: Feat458AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat458Event5(event: Feat458AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat458Event6(event: Feat458AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat458Event7(event: Feat458AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat458Event8(event: Feat458AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat458Event9(event: Feat458AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat458Event10(event: Feat458AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat458Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat458Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat458(u: CoreUser): Feat458Projection1 =
    Feat458Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat458Projection1> {
    val list = java.util.ArrayList<Feat458Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat458(u)
    }
    return list
}
