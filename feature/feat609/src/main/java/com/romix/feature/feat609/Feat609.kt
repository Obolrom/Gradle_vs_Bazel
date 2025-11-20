package com.romix.feature.feat609

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat609Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat609UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat609FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat609UserSummary
)

data class Feat609UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat609NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat609Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat609Config = Feat609Config()
) {

    fun loadSnapshot(userId: Long): Feat609NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat609NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat609UserSummary {
        return Feat609UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat609FeedItem> {
        val result = java.util.ArrayList<Feat609FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat609FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat609UiMapper {

    fun mapToUi(model: List<Feat609FeedItem>): Feat609UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat609UiModel(
            header = UiText("Feat609 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat609UiModel =
        Feat609UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat609UiModel =
        Feat609UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat609UiModel =
        Feat609UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat609Service(
    private val repository: Feat609Repository,
    private val uiMapper: Feat609UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat609UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat609UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat609UserItem1(val user: CoreUser, val label: String)
data class Feat609UserItem2(val user: CoreUser, val label: String)
data class Feat609UserItem3(val user: CoreUser, val label: String)
data class Feat609UserItem4(val user: CoreUser, val label: String)
data class Feat609UserItem5(val user: CoreUser, val label: String)
data class Feat609UserItem6(val user: CoreUser, val label: String)
data class Feat609UserItem7(val user: CoreUser, val label: String)
data class Feat609UserItem8(val user: CoreUser, val label: String)
data class Feat609UserItem9(val user: CoreUser, val label: String)
data class Feat609UserItem10(val user: CoreUser, val label: String)

data class Feat609StateBlock1(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock2(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock3(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock4(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock5(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock6(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock7(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock8(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock9(val state: Feat609UiModel, val checksum: Int)
data class Feat609StateBlock10(val state: Feat609UiModel, val checksum: Int)

fun buildFeat609UserItem(user: CoreUser, index: Int): Feat609UserItem1 {
    return Feat609UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat609StateBlock(model: Feat609UiModel): Feat609StateBlock1 {
    return Feat609StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat609UserSummary> {
    val list = java.util.ArrayList<Feat609UserSummary>(users.size)
    for (user in users) {
        list += Feat609UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat609UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat609UiModel {
    val summaries = (0 until count).map {
        Feat609UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat609UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat609UiModel> {
    val models = java.util.ArrayList<Feat609UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat609AnalyticsEvent1(val name: String, val value: String)
data class Feat609AnalyticsEvent2(val name: String, val value: String)
data class Feat609AnalyticsEvent3(val name: String, val value: String)
data class Feat609AnalyticsEvent4(val name: String, val value: String)
data class Feat609AnalyticsEvent5(val name: String, val value: String)
data class Feat609AnalyticsEvent6(val name: String, val value: String)
data class Feat609AnalyticsEvent7(val name: String, val value: String)
data class Feat609AnalyticsEvent8(val name: String, val value: String)
data class Feat609AnalyticsEvent9(val name: String, val value: String)
data class Feat609AnalyticsEvent10(val name: String, val value: String)

fun logFeat609Event1(event: Feat609AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat609Event2(event: Feat609AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat609Event3(event: Feat609AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat609Event4(event: Feat609AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat609Event5(event: Feat609AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat609Event6(event: Feat609AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat609Event7(event: Feat609AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat609Event8(event: Feat609AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat609Event9(event: Feat609AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat609Event10(event: Feat609AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat609Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat609Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat609(u: CoreUser): Feat609Projection1 =
    Feat609Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat609Projection1> {
    val list = java.util.ArrayList<Feat609Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat609(u)
    }
    return list
}
